package gitv.workflow;

import java.util.List;

public class ExecutionPlan {
    private final List<ExecutionStep> steps;
    private final gitv.workflow.ExecutionMode mode;

    public ExecutionPlan(List<ExecutionStep> steps, gitv.workflow.ExecutionMode mode) {
        this.steps = steps;
        this.mode = mode != null ? mode : gitv.workflow.ExecutionMode.AUTO;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }

    public gitv.workflow.ExecutionMode getMode() {
        return mode;
    }
}
