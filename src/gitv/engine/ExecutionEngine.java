package gitv.engine;

import gitv.git.RepoContext;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowRegistry;
import gitv.workflow.WorkflowResult;

import java.util.*;
import java.util.concurrent.*;

public class ExecutionEngine {
    private final WorkflowRegistry registry;
    private final ExecutorService sandboxExecutor;

    private static final int MAX_TOTAL_STEPS = 50;
    private static final int MAX_QUEUE_SIZE = 10;
    private static final long BASE_DELAY_MS = 1000;
    private static final long MAX_ACTION_EXECUTION_TIME_MS = 1000;
    private static final int MAX_PER_ACTION_EXECUTIONS = 5;

    private final Random random = new Random();

    public ExecutionEngine(WorkflowRegistry registry) {
        this.registry = registry;
        this.sandboxExecutor = new ThreadPoolExecutor(
                4, 8,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public WorkflowResult execute(gitv.workflow.ExecutionPlan plan, RepoContext repoContext) {
        ExecutionLogger logger = new ExecutionLogger(System.getProperty("gitv.debug") != null);

        List<ActionKey> initialActions = plan != null && plan.getSteps() != null ? 
            plan.getSteps().stream().map(gitv.workflow.ExecutionStep::getAction).toList() : Collections.emptyList();

        if (initialActions.isEmpty()
                || (initialActions.size() == 1 && initialActions.get(0) == ActionKey.NONE)) {
            logger.logFinalSummary(true, "No actions to execute.");
            return new WorkflowResult(true, "No actions to execute.");
        }

        ExecutionContext context = new ExecutionContext(repoContext);
        Deque<ActionKey> queue = new LinkedList<>();
        Set<ActionKey> inQueue = new HashSet<>();
        Map<ActionKey, Integer> executionCount = new HashMap<>();
        Map<ActionKey, Integer> retryCount = new HashMap<>();

        for (ActionKey key : initialActions) {
            if (!safeEnqueue(queue, inQueue, key, false, logger)) {
                return failSafely(logger, context, key, "Queue max size exceeded on initial enqueue");
            }
        }

        int totalSteps = 0;

        while (!queue.isEmpty()) {
            if (++totalSteps > MAX_TOTAL_STEPS) {
                return failSafely(logger, context, null, "Global execution step limit exceeded");
            }

            ActionKey action = queue.poll();
            if (action == null) {
                break;
            }
            inQueue.remove(action);

            if (action == ActionKey.NONE)
                continue;

            Workflow workflow = registry.get(action);
            if (workflow == null) {
                return failSafely(logger, context, action, "No workflow registered");
            }

            int execCount = executionCount.getOrDefault(action, 0) + 1;
            executionCount.put(action, execCount);

            if (execCount > MAX_PER_ACTION_EXECUTIONS) {
                return failSafely(logger, context, action, "Hard execution loop limit exceeded for action: " + action);
            }

            int retries = retryCount.getOrDefault(action, 0);
            if (retries > workflow.getMaxRetries()) {
                return failSafely(logger, context, action, "Max retries exceeded for action: " + action);
            }

            logger.logStart(action);
            WorkflowResult result;
            if (plan.getMode() == gitv.workflow.ExecutionMode.INTERACTIVE) {
                result = executeActionInteractive(workflow, context, logger);
            } else {
                result = executeActionSafely(workflow, context, logger);
            }

            if (result.getNextAction() != null && !registry.contains(result.getNextAction())) {
                return failSafely(logger, context, action, "Invalid next action");
            }

            if (result.isSuccess()) {
                logger.logSuccess(action, result.getMessage());
                executionCount.remove(action);
                retryCount.remove(action);
            } else {
                FailureCategory category = result.getFailureCategory();
                logger.logFailure(action, category, result.getMessage());

                if (category == FailureCategory.SECURITY_VIOLATION || category == FailureCategory.FATAL_ERROR) {
                    executionCount.remove(action);
                    retryCount.remove(action);
                    logger.logFinalSummary(false, result.getMessage());
                    return result;
                }

                if (category == FailureCategory.RECOVERABLE_ERROR) {
                    if (result.getNextAction() != null) {
                        logger.logRecoveryInjection(action, result.getNextAction());
                        retryCount.remove(result.getNextAction());
                        if (!safeEnqueue(queue, inQueue, result.getNextAction(), true, logger)) {
                            return failSafely(logger, context, action, "Queue max size exceeded during recovery");
                        }
                    } else {
                        long exponential = Math.min(1L << retries, 1L << 10);
                        long delay = BASE_DELAY_MS * exponential + random.nextInt(500);
                        logger.logRetry(action, retries + 1, delay);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return failSafely(logger, context, action,
                                    "Execution engine interrupted during backoff sleep");
                        }
                        retryCount.put(action, retries + 1);
                        if (!safeEnqueue(queue, inQueue, action, false, logger)) {
                            return failSafely(logger, context, action, "Queue max size exceeded during retry");
                        }
                    }
                }
            }
        }

        logger.logFinalSummary(true, "All actions executed successfully.");
        return new WorkflowResult(true, "All actions executed successfully.");
    }

    private WorkflowResult failSafely(ExecutionLogger logger, ExecutionContext context, ActionKey action,
            String error) {
        if (action != null)
            logger.logFailure(action, FailureCategory.FATAL_ERROR, error);
        logger.logFinalSummary(false, error);
        return new WorkflowResult(false, error, null, FailureCategory.FATAL_ERROR);
    }

    private boolean safeEnqueue(Deque<ActionKey> queue, Set<ActionKey> inQueue, ActionKey nextAction,
            boolean injectFirst, ExecutionLogger logger) {
        if (nextAction == null || nextAction == ActionKey.NONE)
            return true;

        if (queue.size() >= MAX_QUEUE_SIZE) {
            return false;
        }

        if (inQueue.contains(nextAction)) {
            logger.logDebug("Anti-Spam: Skipping already queued action " + nextAction);
            return true;
        }

        if (injectFirst) {
            queue.addFirst(nextAction);
        } else {
            queue.addLast(nextAction);
        }
        inQueue.add(nextAction);
        return true;
    }

    private WorkflowResult executeActionSafely(Workflow workflow, ExecutionContext context, ExecutionLogger logger) {
        Future<WorkflowResult> future;
        try {
            future = sandboxExecutor.submit(() -> workflow.execute(context));
        } catch (RejectedExecutionException e) {
            return new WorkflowResult(false, "Thread pool exhausted", null, FailureCategory.SECURITY_VIOLATION);
        }

        try {
            long timeout = MAX_ACTION_EXECUTION_TIME_MS;
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            boolean cancelled = future.cancel(true);
            if (!cancelled) {
                logger.logDebug("Failed to cancel task, potential zombie thread");
            }
            return new WorkflowResult(false, "Timeout elapsed", null, FailureCategory.SECURITY_VIOLATION);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new WorkflowResult(false, "Orchestrator interrupted", null, FailureCategory.FATAL_ERROR);
        } catch (ExecutionException e) {
            return mapSandboxException(e.getCause());
        }
    }

    private WorkflowResult executeActionInteractive(Workflow workflow, ExecutionContext context, ExecutionLogger logger) {
        try {
            WorkflowResult result = workflow.execute(context);
            // Post-interactive observability hook: Re-scan state
            gitv.git.ContextBuilder builder = new gitv.git.ContextBuilder();
            gitv.git.RepoContext newContext = builder.build();
            logger.logDebug("Post-Interactive State: hasUnstagedChanges=" + newContext.hasUnstagedChanges() + 
                            ", hasStagedChanges=" + newContext.hasStagedChanges() + 
                            ", isAhead=" + newContext.isAheadOfRemote() + 
                            ", isBehind=" + newContext.isBehindRemote());
            return result;
        } catch (Exception e) {
            return mapSandboxException(e);
        }
    }

    private WorkflowResult mapSandboxException(Throwable cause) {
        if (cause instanceof SecurityException) {
            return new WorkflowResult(false, "Security Violation: " + cause.getMessage(), null,
                    FailureCategory.SECURITY_VIOLATION);
        }
        if (cause instanceof NullPointerException || cause instanceof IllegalArgumentException) {
            return new WorkflowResult(false, "Fatal Workflow Bug: " + cause.getMessage(), null,
                    FailureCategory.FATAL_ERROR);
        }
        return new WorkflowResult(false, "Unhandled Sandbox Crash: " + cause.getMessage(), null,
                FailureCategory.FATAL_ERROR);
    }
}
