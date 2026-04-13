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

    public ScoredAction decide(RepoContext context) {
        List<ScoredAction> actions = new ArrayList<>();

        for (ActionEvaluator evaluator : evaluators) {
            actions.add(evaluator.evaluate(context));
        }

        return actions.stream()
            .max(Comparator.comparingDouble(ScoredAction::getScore))
            .filter(a -> a.getScore() > 0)
            .orElse(ScoredAction.none());
    }
}
