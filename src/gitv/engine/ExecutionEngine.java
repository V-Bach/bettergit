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

    public WorkflowResult execute(List<ActionType> initialActions, RepoContext repoContext) {
        if (initialActions == null || initialActions.isEmpty() || (initialActions.size() == 1 && initialActions.get(0) == ActionType.NONE)) {
            return new WorkflowResult(true, "No actions to execute.");
        }

        ExecutionContext context = new ExecutionContext(repoContext);
        Deque<ActionType> queue = new LinkedList<>(initialActions);

        while (!queue.isEmpty()) {
            ActionType action = queue.poll();
            if (action == ActionType.NONE) continue;
            
            Workflow workflow = registry.getWorkflow(action);
            if (workflow == null) {
                return new WorkflowResult(false, "Unknown action: " + action);
            }

            int count = context.getExecutionCount().getOrDefault(action, 0) + 1;
            context.getExecutionCount().put(action, count);

            if (count > MAX_RETRY) {
                String error = "Max retries exceeded for action: " + action;
                System.out.println("[EXEC] " + action + " -> FATAL -> STOP (" + error + ")");
                context.getHistory().add(new ExecutionRecord(action, false, FailureType.FATAL, error));
                return new WorkflowResult(false, error, false, null, FailureType.FATAL);
            }

            System.out.println("[EXEC] " + action + " -> START");
            context.clearStepData();
            WorkflowResult result = workflow.execute(context);
            context.getHistory().add(new ExecutionRecord(action, result.isSuccess(), result.getFailureType(), result.getMessage()));

            if (result.isSuccess()) {
                System.out.println("[EXEC] " + action + " -> SUCCESS");
                context.getExecutedActions().add(action);
            } else {
                FailureType failureType = result.getFailureType();
                if (failureType == FailureType.TRANSIENT) {
                    long delay = BASE_DELAY_MS * (1L << (count - 1)) + random.nextInt(500);
                    System.out.println("[EXEC] " + action + " -> RETRY #" + count + " after " + delay + "ms");
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    queue.addFirst(action);
                } else if (failureType == FailureType.CONFLICT && result.getNextAction() != null) {
                    System.out.println("[EXEC] " + action + " -> CONFLICT -> RECOVERY: " + result.getNextAction());
                    queue.addFirst(action);
                    queue.addFirst(result.getNextAction());
                } else if (failureType == FailureType.FATAL) {
                    if (result.isBlocking()) {
                        System.out.println("[EXEC] " + action + " -> FATAL -> STOP");
                        return result; 
                    } else {
                        System.out.println("[EXEC] " + action + " -> FATAL (Non-blocking) -> CONTINUE");
                    }
                } else {
                    return result; // Stop execution on unhandled
                }
            }
        }
        return new WorkflowResult(true, "All actions executed successfully.");
    }
}
