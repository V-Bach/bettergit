package gitv.engine;

public class ExecutionRecord {
    private final ActionKey action;
    private final boolean success;
    private final FailureCategory FailureCategory;
    private final String message;
    private final long timestamp;

    public ExecutionRecord(ActionKey action, boolean success, FailureCategory FailureCategory, String message) {
        this.action = action;
        this.success = success;
        this.FailureCategory = FailureCategory;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ActionKey getAction() {
        return action;
    }

    public boolean isSuccess() {
        return success;
    }

    public FailureCategory getFailureType() {
        return FailureCategory;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
