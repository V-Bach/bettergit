package gitv.engine;

import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionContext {
    private final RepoContext repoContext;
    private final List<ActionType> executedActions;
    private final Map<String, Object> sharedData;
    private final Map<String, Object> stepData;
    private final Map<ActionType, Integer> executionCount;
    private final List<ExecutionRecord> history;

    public ExecutionContext(RepoContext repoContext) {
        this.repoContext = repoContext;
        this.executedActions = new ArrayList<>();
        this.sharedData = new HashMap<>();
        this.stepData = new HashMap<>();
        this.executionCount = new HashMap<>();
        this.history = new ArrayList<>();
    }

    public RepoContext getRepoContext() {
        return repoContext;
    }

    public List<ActionType> getExecutedActions() {
        return executedActions;
    }

    public Map<String, Object> getSharedData() {
        return sharedData;
    }

    public Map<String, Object> getStepData() {
        return stepData;
    }

    public void clearStepData() {
        stepData.clear();
    }

    public Map<ActionType, Integer> getExecutionCount() {
        return executionCount;
    }

    public List<ExecutionRecord> getHistory() {
        return history;
    }
}
