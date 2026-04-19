package gitv.workflow;

import java.util.List;

public class ExecutionPlan {
    private final List<ExecutionStep> steps;

    public ExecutionPlan(List<ExecutionStep> steps) {
        this.steps = steps;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }
}
