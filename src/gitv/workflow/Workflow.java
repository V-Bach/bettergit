package gitv.workflow;

import gitv.engine.ActionType;
import gitv.engine.ExecutionContext;

public interface Workflow {
    ActionType getType();
    WorkflowResult execute(ExecutionContext context);
}
