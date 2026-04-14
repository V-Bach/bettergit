package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.List;

public class PushEvaluator implements ActionEvaluator {
    @Override
    public ScoredAction evaluate(RepoContext context) {
        if (context.isBehindRemote()) {
            return ScoredAction.blocked(ActionKey.PUSH, "Repository is behind remote. Must pull/sync first.");
        }

        double score = 0.0;
        int priority = 80; // GLOBAL PRIORITY POLICY: PUSH -> 80
        List<String> reasons = new ArrayList<>();

        if (context.hasUnpushedCommits()) {
            score += 1.0;
            reasons.add("Local branch is ahead of remote with unpushed commits (+1.0)");
        }
        
        if (context.isAheadOfRemote() && !context.hasUnpushedCommits()) {
            score += 1.0; 
            reasons.add("Local branch is structurally ahead of remote (+1.0)");
        }

        return new ScoredAction(ActionKey.PUSH, score, priority, false, reasons);
    }
}

