package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;
import gitv.suggestion.DecisionResult;
import gitv.suggestion.ScoredAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanBuilder {

    public ExecutionPlan build(DecisionResult decision, RepoContext context) {
        if (decision == null || decision.getSelected() == null) {
            return new ExecutionPlan(Collections.emptyList());
        }

        ScoredAction selected = decision.getSelected();
        ActionKey action = selected.getType();
        List<ExecutionStep> steps = new ArrayList<>();

        if (action == ActionKey.COMMIT) {
            steps.add(new ExecutionStep(ActionKey.COMMIT, selected.getReasons()));
            steps.add(new ExecutionStep(ActionKey.PUSH, Collections.singletonList("Added as follow-up to COMMIT")));
        } else if (action == ActionKey.PUSH) {
            if (context.isBehindRemote()) {
                steps.add(new ExecutionStep(ActionKey.PULL, Collections.singletonList("PULL required before PUSH (remote ahead)")));
                steps.add(new ExecutionStep(ActionKey.PUSH, selected.getReasons()));
            } else {
                steps.add(new ExecutionStep(ActionKey.PUSH, selected.getReasons()));
            }
        } else if (action == ActionKey.PULL) {
            steps.add(new ExecutionStep(ActionKey.PULL, selected.getReasons()));
        } else if (action == ActionKey.NONE) {
            steps.add(new ExecutionStep(ActionKey.NONE, Collections.singletonList("No safe actions available")));
        } else {
            steps.add(new ExecutionStep(action, selected.getReasons()));
        }

        return new ExecutionPlan(steps);
    }
}
