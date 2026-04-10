package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.engine.FailureType;

public class WorkflowResult {
    private final boolean success;
    private final String message;
    private final boolean retryable;
    private final ActionKey nextAction;
    private final FailureType failureType;
    private final boolean blocking;

    public WorkflowResult(boolean success, String message) {
        this(success, message, false, null, success ? FailureType.NONE : FailureType.FATAL);
    }

    public WorkflowResult(boolean success, String message, boolean retryable, ActionKey nextAction) {
        this(success, message, retryable, nextAction, success ? FailureType.NONE : (retryable ? FailureType.TRANSIENT : FailureType.FATAL));
    }

    public WorkflowResult(boolean success, String message, boolean retryable, ActionKey nextAction, FailureType failureType) {
        this(success, message, retryable, nextAction, failureType, true); // default blocking = true
    }

    public WorkflowResult(boolean success, String message, boolean retryable, ActionKey nextAction, FailureType failureType, boolean blocking) {
        this.success = success;
        this.message = message;
        this.retryable = retryable;
        this.nextAction = nextAction;
        this.failureType = failureType;
        this.blocking = blocking;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public ActionKey getNextAction() {
        return nextAction;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public boolean isBlocking() {
        return blocking;
    }
}
