package gitv.workflow;

public class SafetyResult {
    private final boolean isSafe;
    private final String message;
    private final RiskLevel riskLevel;

    private SafetyResult(boolean isSafe, String message, RiskLevel riskLevel) {
        this.isSafe = isSafe;
        this.message = message;
        this.riskLevel = riskLevel;
    }

    public static SafetyResult safe(RiskLevel riskLevel) {
        return new SafetyResult(true, "Safe to execute", riskLevel);
    }

    public static SafetyResult failure(String message) {
        return new SafetyResult(false, message, RiskLevel.CRITICAL);
    }

    public boolean isSafe() {
        return isSafe;
    }

    public String getMessage() {
        return message;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
}
