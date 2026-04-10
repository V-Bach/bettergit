package gitv.engine;

import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionContext {
    private final RepoContext repoContext;
    private final List<ActionKey> executedActions;
    private final Map<String, Object> sharedData;
    private final Map<String, Object> stepData;
    private final Map<ActionKey, Integer> executionCount;
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

    public List<ActionKey> getExecutedActions() {
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

    public Map<ActionKey, Integer> getExecutionCount() {
        return executionCount;
    }

    public List<ExecutionRecord> getHistory() {
        return history;
    }
}
