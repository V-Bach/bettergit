package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.engine.FailureCategory;

public class WorkflowResult {
    private final boolean success;
    private final ActionKey nextAction;
    private final FailureCategory failureCategory;
    private final String message;

    public WorkflowResult(boolean success, String message, ActionKey nextAction, FailureCategory failureCategory) {
        this.success = success;
        this.message = message;
        this.nextAction = nextAction;
        this.failureCategory = failureCategory;
    }

    public WorkflowResult(boolean success, String message) {
        this(success, message, null, success ? FailureCategory.NONE : FailureCategory.FATAL_ERROR);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ActionKey getNextAction() {
        return nextAction;
    }

    public FailureCategory getFailureCategory() {
        return failureCategory;
    }
}
