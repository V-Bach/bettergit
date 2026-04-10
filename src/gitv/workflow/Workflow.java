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
        return true;
    }
}
