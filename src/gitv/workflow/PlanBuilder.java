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

        boolean hasBlockedPush = decision.getAlternatives().stream()
                .anyMatch(a -> a.getType() == ActionKey.PUSH && a.isBlocked());

        if (action == ActionKey.NONE) {
            if (hasBlockedPush) {
                steps.add(new ExecutionStep(ActionKey.PULL, Collections.singletonList("Fallback PULL for blocked PUSH intent")));
                steps.add(new ExecutionStep(ActionKey.PUSH, Collections.singletonList("Executing previously blocked PUSH")));
            }
        } else if (action == ActionKey.COMMIT) {
            if (hasBlockedPush) {
                steps.add(new ExecutionStep(ActionKey.PULL, Collections.singletonList("Syncing remote before local commit to prevent topology crash")));
                steps.add(new ExecutionStep(ActionKey.COMMIT, selected.getReasons()));
                steps.add(new ExecutionStep(ActionKey.PUSH, Collections.singletonList("Added as follow-up to COMMIT")));
            } else {
                steps.add(new ExecutionStep(ActionKey.COMMIT, selected.getReasons()));
                steps.add(new ExecutionStep(ActionKey.PUSH, Collections.singletonList("Added as follow-up to COMMIT")));
            }
        } else if (action == ActionKey.PULL) {
            steps.add(new ExecutionStep(ActionKey.PULL, selected.getReasons()));
        } else if (action == ActionKey.PUSH) {
            steps.add(new ExecutionStep(ActionKey.PUSH, selected.getReasons()));
        } else {
            steps.add(new ExecutionStep(action, selected.getReasons()));
        }

        return new ExecutionPlan(steps);
    }
}
