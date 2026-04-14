package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.List;

public class CommitEvaluator implements ActionEvaluator {
    @Override
    public ScoredAction evaluate(RepoContext context) {
        double score = 0.0;
        int priority = 100; // GLOBAL PRIORITY POLICY: COMMIT -> 100
        List<String> reasons = new ArrayList<>();

        if (context.hasStagedChanges()) {
            score += 1.0;
            reasons.add("Staged changes ready to commit (+1.0)");
        }

        if (context.hasUnstagedChanges()) {
            score -= 0.5; // Warning level penalty
            reasons.add("Unstaged changes present (-0.5)");
            if (!context.hasStagedChanges()) {
                score += 0.8; 
                reasons.add("Fallback: Unstaged changes available to stage and commit (+0.8)");
            }
        }

        return new ScoredAction(ActionKey.COMMIT, score, priority, false, reasons);
    }
}

