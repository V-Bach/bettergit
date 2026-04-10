package gitv.workflow;

import gitv.engine.ExecutionContext;

public interface Workflow {
    WorkflowResult execute(ExecutionContext context);
}
