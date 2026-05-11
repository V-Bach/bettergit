package gitv.workflow;

import gitv.engine.ActionKey;
import java.util.List;

public class ExecutionStep {
    private final ActionKey action;
    private final List<String> reasons;

    public ExecutionStep(ActionKey action, List<String> reasons) {
        this.action = action;
        this.reasons = reasons;
    }

    public ActionKey getAction() {
        return action;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
