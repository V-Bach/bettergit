package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.List;

public class DecisionEngine {

    public ScoredAction decide(RepoContext context) {
        ScoredAction commitAction = evaluateCommit(context);
        ScoredAction pushAction = evaluatePush(context);

        // Selection logic: return the one with the highest score
        if (commitAction.getScore() > pushAction.getScore() && commitAction.getScore() > 0) {
            return commitAction;
        } else if (pushAction.getScore() > 0) {
            return pushAction;
        }

        return new ScoredAction(ActionKey.NONE, 0.0, List.of("Clean working tree, nothing to do"));
    }

    private ScoredAction evaluateCommit(RepoContext context) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();

        if (context.hasChanges()) {
            score += 1.0;
            reasons.add("hasChanges = true");
        }

        return new ScoredAction(ActionKey.COMMIT, score, reasons);
    }

    private ScoredAction evaluatePush(RepoContext context) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();

        if (context.hasUnpushedCommits()) {
            score += 1.0;
            reasons.add("hasUnpushedCommits = true");
        }

        return new ScoredAction(ActionKey.PUSH, score, reasons);
    }
}
