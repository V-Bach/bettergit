package gitv.suggestion;

import java.util.List;

public class DecisionResult {
    private final ScoredAction selected;
    private final List<ScoredAction> alternatives;

    public DecisionResult(ScoredAction selected, List<ScoredAction> alternatives) {
        this.selected = selected;
        this.alternatives = alternatives;
    }

    public ScoredAction getSelected() {
        return selected;
    }

    public List<ScoredAction> getAlternatives() {
        return alternatives;
    }
}
