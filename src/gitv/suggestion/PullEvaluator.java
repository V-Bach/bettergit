package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

import java.util.ArrayList;
import java.util.List;

public class PullEvaluator implements ActionEvaluator {
    @Override
    public ScoredAction evaluate(RepoContext context) {
        double score = 0.0;
        int priority = 90; // GLOBAL PRIORITY POLICY: COMMIT(100) -> PULL(90) -> PUSH(80)
        List<String> reasons = new ArrayList<>();

        if (context.isBehindRemote()) {
            score += 1.0;
            reasons.add("Repository is behind remote, PULL needed to sync (+1.0)");
        }

        return new ScoredAction(ActionKey.PULL, score, priority, false, reasons);
    }
}
