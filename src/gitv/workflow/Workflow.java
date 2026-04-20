package gitv.workflow;

import gitv.engine.ExecutionContext;

public interface Workflow {
    WorkflowResult execute(ExecutionContext context);

    default int getMaxRetries() {
        return 3;
    }

    default long getExpectedExecutionTimeMs() {
        return 500;
    }

    default boolean isIdempotent() {
        return  true;
    }
}
// This code defines a Workflow interface with an execute method that takes an ExecutionContext and returns a WorkflowResult. It also includes default methods for getting the maximum number of retries, expected execution time, and whether the workflow is idempotent.
