package gitv.suggestion;

import gitv.engine.ActionKey;
import java.util.List;
import java.util.Collections;

public class ScoredAction {
    private final ActionKey type;
    private final double score;
    private final int priority;
    private final boolean blocked;
    private final List<String> reasons;

    public ScoredAction(ActionKey type, double score, int priority, boolean blocked, List<String> reasons) {
        this.type = type;
        this.score = score;
        this.priority = priority;
        this.blocked = blocked;
        this.reasons = reasons;
    }

    public static ScoredAction none() {
        return new ScoredAction(ActionKey.NONE, 0.0, 0, false, Collections.singletonList("No safe actions available or repository is completely clean"));
    }

    public static ScoredAction blocked(ActionKey type, String reason) {
        return new ScoredAction(type, -1.0, 0, true, Collections.singletonList("BLOCKED: " + reason));
    }

    public ActionKey getType() {
        return type;
    }

    public double getScore() {
        return score;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
