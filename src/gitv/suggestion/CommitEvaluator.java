package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.List;

public class CommitEvaluator implements ActionEvaluator {
    @Override
    public ScoredAction evaluate(RepoContext context) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();

        if (context.hasUnstagedChanges() || context.hasStagedChanges()) {
            score += 1.0;
            reasons.add("Working directory has modifications");
        }

        return new ScoredAction(ActionKey.COMMIT, score, reasons);
    }
}
