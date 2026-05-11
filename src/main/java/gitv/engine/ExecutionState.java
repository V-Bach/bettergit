package gitv.engine;

import java.util.ArrayList;
import java.util.List;

public class ExecutionState {
    private final String executionId;
    private final String initialHeadHash;
    private final List<ActionKey> plannedActions;
    private final List<ActionKey> completedSteps;

    public ExecutionState(String executionId, String initialHeadHash, List<ActionKey> plannedActions, List<ActionKey> completedSteps) {
        this.executionId = executionId;
        this.initialHeadHash = initialHeadHash;
        this.plannedActions = plannedActions != null ? plannedActions : new ArrayList<>();
        this.completedSteps = completedSteps != null ? completedSteps : new ArrayList<>();
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getInitialHeadHash() {
        return initialHeadHash;
    }

    public List<ActionKey> getPlannedActions() {
        return plannedActions;
    }

    public List<ActionKey> getCompletedSteps() {
        return completedSteps;
    }
}
