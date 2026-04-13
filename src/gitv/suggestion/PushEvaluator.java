package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.List;

public class PushEvaluator implements ActionEvaluator {
    @Override
    public ScoredAction evaluate(RepoContext context) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();

        if (context.hasUnpushedCommits() || context.isAheadOfRemote()) {
            score += 1.0;
            reasons.add("Local branch is ahead of remote");
        }

        return new ScoredAction(ActionKey.PUSH, score, reasons);
    }
}
