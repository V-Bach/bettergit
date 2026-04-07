package gitv.engine;

public class ExecutionRecord {
    private final ActionType action;
    private final boolean success;
    private final FailureType failureType;
    private final String message;
    private final long timestamp;

    public ExecutionRecord(ActionType action, boolean success, FailureType failureType, String message) {
        this.action = action;
        this.success = success;
        this.failureType = failureType;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ActionType getAction() {
        return action;
    }

    public boolean isSuccess() {
        return success;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
