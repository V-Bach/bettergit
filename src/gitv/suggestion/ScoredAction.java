package gitv.suggestion;

import gitv.engine.ActionKey;
import java.util.List;

public class ScoredAction {
    private final ActionKey type;
    private final double score;
    private final List<String> reasons;

    public ScoredAction(ActionKey type, double score, List<String> reasons) {
        this.type = type;
        this.score = score;
        this.reasons = reasons;
    }

    public ActionKey getType() {
        return type;
    }

    public double getScore() {
        return score;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
