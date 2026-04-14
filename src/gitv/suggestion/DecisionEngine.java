package gitv.suggestion;

import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecisionEngine {

    private final List<ActionEvaluator> evaluators;

    public DecisionEngine() {
        this.evaluators = new ArrayList<>();
        this.evaluators.add(new CommitEvaluator());
        this.evaluators.add(new PushEvaluator());
    }

    public DecisionResult decide(RepoContext context) {
        List<ScoredAction> actions = new ArrayList<>();

        for (ActionEvaluator evaluator : evaluators) {
            actions.add(evaluator.evaluate(context));
        }

        actions.sort(Comparator.comparingDouble(ScoredAction::getScore)
            .thenComparingInt(ScoredAction::getPriority)
            .reversed());

        ScoredAction selected = null;
        List<ScoredAction> alternatives = new ArrayList<>();

        for (ScoredAction action : actions) {
            if (selected == null && !action.isBlocked() && action.getScore() > 0) {
                selected = action;
            } else {
                alternatives.add(action);
            }
        }

        if (selected == null) {
            selected = ScoredAction.none();
        }

        return new DecisionResult(selected, alternatives);
    }
}

