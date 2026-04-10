package gitv.engine;

import gitv.git.RepoContext;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowRegistry;
import gitv.workflow.WorkflowResult;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ExecutionEngine {
    private final WorkflowRegistry registry;
    private static final int MAX_RETRY = 3;
    private static final long BASE_DELAY_MS = 1000;
    private final Random random = new Random();

    public ExecutionEngine(WorkflowRegistry registry) {
        this.registry = registry;
    }

    public WorkflowResult execute(List<ActionKey> initialActions, RepoContext repoContext) {
        ExecutionLogger logger = new ExecutionLogger(System.getProperty("gitv.debug") != null);

        if (initialActions == null || initialActions.isEmpty() || (initialActions.size() == 1 && initialActions.get(0) == ActionKey.NONE)) {
            logger.logFinalSummary(true, "No actions to execute.");
            return new WorkflowResult(true, "No actions to execute.");
        }

        ExecutionContext context = new ExecutionContext(repoContext);
        Deque<ActionKey> queue = new LinkedList<>(initialActions);

        while (!queue.isEmpty()) {
            ActionKey action = queue.poll();
            if (action == ActionKey.NONE) continue;
            
            Workflow workflow = registry.get(action);
            if (workflow == null) {
                WorkflowResult result = new WorkflowResult(false, "No workflow registered", false, null, FailureType.FATAL);
                logger.logFailure(action, FailureType.FATAL, result.getMessage());
                logger.logFinalSummary(false, result.getMessage());
                return result;
            }

            int count = context.getExecutionCount().getOrDefault(action, 0) + 1;
            context.getExecutionCount().put(action, count);

            if (count > MAX_RETRY) {
                String error = "Max retries exceeded for action: " + action;
                logger.logFailure(action, FailureType.FATAL, error);
                context.getHistory().add(new ExecutionRecord(action, false, FailureType.FATAL, error));
                WorkflowResult result = new WorkflowResult(false, error, false, null, FailureType.FATAL);
                logger.logFinalSummary(false, error);
                return result;
            }

            logger.logStart(action);
            context.clearStepData();
            WorkflowResult result = workflow.execute(context);
            
            if (result.getNextAction() != null && !registry.contains(result.getNextAction())) {
                String error = "Invalid next action";
                logger.logFailure(action, FailureType.FATAL, error);
                context.getHistory().add(new ExecutionRecord(action, false, FailureType.FATAL, error));
                WorkflowResult fatalResult = new WorkflowResult(false, error, false, null, FailureType.FATAL);
                logger.logFinalSummary(false, error);
                return fatalResult;
            }
            
            context.getHistory().add(new ExecutionRecord(action, result.isSuccess(), result.getFailureType(), result.getMessage()));

            if (result.isSuccess()) {
                logger.logSuccess(action, result.getMessage());
                context.getExecutedActions().add(action);
            } else {
                FailureType failureType = result.getFailureType();
                logger.logFailure(action, failureType, result.getMessage());

                if (failureType == FailureType.TRANSIENT) {
                    long delay = BASE_DELAY_MS * (1L << (count - 1)) + random.nextInt(500);
                    logger.logRetry(action, count, delay);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    queue.addFirst(action);
                } else if (failureType == FailureType.CONFLICT && result.getNextAction() != null) {
                    logger.logRecoveryInjection(action, result.getNextAction());
                    queue.addFirst(action);
                    queue.addFirst(result.getNextAction());
                } else if (failureType == FailureType.FATAL) {
                    if (result.isBlocking()) {
                        logger.logDebug("Fatal error is blocking, stopping execution.");
                        logger.logFinalSummary(false, result.getMessage());
                        return result; 
                    } else {
                        logger.logDebug("Fatal error is non-blocking, continuing execution.");
                    }
                } else {
                    logger.logFinalSummary(false, result.getMessage());
                    return result; // Stop execution on unhandled
                }
            }
        }
        
        logger.logFinalSummary(true, "All actions executed successfully.");
        return new WorkflowResult(true, "All actions executed successfully.");
    }
}
