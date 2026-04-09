package gitv.engine;

public class ExecutionLogger {
    private final boolean debugMode;
    private final long startTime;
    private int totalSteps;
    private int totalRetries;

    // ANSI escape codes for lightweight CLI colors
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    public ExecutionLogger(boolean debugMode) {
        this.debugMode = debugMode;
        this.startTime = System.currentTimeMillis();
        this.totalSteps = 0;
        this.totalRetries = 0;
    }

    public void logStart(ActionType action) {
        System.out.printf("%s▶%s %s%s%s%n", BLUE, RESET, BOLD, action, RESET);
    }

    public void logSuccess(ActionType action, String message) {
        this.totalSteps++;
        if (debugMode && message != null && !message.isEmpty()) {
            System.out.printf("   %s✓%s Success - %s%n", GREEN, RESET, message);
        } else {
            System.out.printf("   %s✓%s Success%n", GREEN, RESET);
        }
    }

    public void logFailure(ActionType action, FailureType type, String message) {
        String color = (type == FailureType.FATAL) ? RED : YELLOW;
        System.out.printf("   %s✗%s %s[%s]%s Failed: %s%n", color, RESET, color, type, RESET, message);
    }

    public void logRetry(ActionType action, int attempt, long delayMs) {
        this.totalRetries++;
        System.out.printf("   %s↻%s Retrying in %dms (Attempt %d)...%n", YELLOW, RESET, delayMs, attempt);
    }

    public void logRecoveryInjection(ActionType failedAction, ActionType recoveryAction) {
        System.out.printf("   %s↳%s Injecting recovery action: %s%s%n", CYAN, RESET, BOLD, recoveryAction, RESET);
    }

    public void logDebug(String message) {
        if (debugMode) {
            System.out.printf("   %s[DEBUG]%s %s%n", CYAN, RESET, message);
        }
    }

    public void logFinalSummary(boolean isSuccess, String resultMessage) {
        long duration = System.currentTimeMillis() - startTime;
        String headerColor = isSuccess ? GREEN : RED;
        
        System.out.println();
        System.out.println(headerColor + "=== Execution Summary ===" + RESET);
        System.out.println("Result   : " + (isSuccess ? "Success" : "Failed") + " (" + resultMessage + ")");
        System.out.println("Duration : " + duration + "ms");
        System.out.println("Steps    : " + totalSteps + " successful steps");
        System.out.println("Retries  : " + totalRetries);
        System.out.println(headerColor + "=========================" + RESET);
    }
}
