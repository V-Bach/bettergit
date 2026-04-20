package gitv.workflow;

public class SafetyResult {
    private final boolean isSafe;
    private final String message;

    private SafetyResult(boolean isSafe, String message) {
        this.isSafe = isSafe;
        this.message = message;
    }

    public static SafetyResult safe() {
        return new SafetyResult(true, "Safe to execute");
    }

    public static SafetyResult failure(String message) {
        return new SafetyResult(false, message);
    }

    public boolean isSafe() {
        return isSafe;
    }

    public String getMessage() {
        return message;
    }
}
