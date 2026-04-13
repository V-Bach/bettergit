package gitv.suggestion;

import gitv.engine.ActionKey;
import java.util.List;
import java.util.Collections;

public class ScoredAction {
    private final ActionKey type;
    private final double score;
    private final List<String> reasons;

    public ScoredAction(ActionKey type, double score, List<String> reasons) {
        this.type = type;
        this.score = score;
        this.reasons = reasons;
    }

    public static ScoredAction none() {
        return new ScoredAction(ActionKey.NONE, 0.0, Collections.singletonList("Clean working tree, nothing to do"));
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
