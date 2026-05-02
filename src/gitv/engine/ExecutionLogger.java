package gitv.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExecutionLogger {
    private final boolean debugMode;
    private final long startTime;
    private final String executionId;
    private final File logFile;
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

    public ExecutionLogger(boolean debugMode, String executionId, File logFile) {
        this.debugMode = debugMode;
        this.startTime = System.currentTimeMillis();
        this.executionId = executionId;
        this.logFile = logFile;
        this.totalSteps = 0;
        this.totalRetries = 0;

        if (logFile != null) {
            logFile.getParentFile().mkdirs();
        }
    }

    private void appendToFile(String message) {
        if (logFile == null) return;
        String cleanMessage = message.replaceAll("\u001B\\[[;\\d]*m", "");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.printf("[%s] [%s] %s%n", timestamp, executionId, cleanMessage);
        } catch (IOException e) {
            // Ignore file logging errors to prevent breaking CLI flow
        }
    }

    public void logStart(ActionKey action) {
        String msg = String.format("%s▶%s %s%s%s", BLUE, RESET, BOLD, action, RESET);
        System.out.println(msg);
        appendToFile(String.format("Executing Step: %s", action));
    }

    public void logSuccess(ActionKey action, String message) {
        this.totalSteps++;
        String msg;
        if (debugMode && message != null && !message.isEmpty()) {
            msg = String.format("   %s✓%s Success - %s", GREEN, RESET, message);
        } else {
            msg = String.format("   %s✓%s Success", GREEN, RESET);
        }
        System.out.println(msg);
        appendToFile(String.format("Step %s Status: Success%s", action, (message != null && !message.isEmpty() ? " - " + message : "")));
    }

    public void logFailure(ActionKey action, FailureCategory type, String message) {
        String color = (type == FailureCategory.FATAL_ERROR) ? RED : YELLOW;
        String msg = String.format("   %s✗%s %s[%s]%s Failed: %s", color, RESET, color, type, RESET, message);
        System.out.println(msg);
        appendToFile(String.format("Step %s Status: Failed [%s] - %s", action, type, message));
    }

    public void logRetry(ActionKey action, int attempt, long delayMs) {
        this.totalRetries++;
        String msg = String.format("   %s↻%s Retrying in %dms (Attempt %d)...", YELLOW, RESET, delayMs, attempt);
        System.out.println(msg);
        appendToFile(String.format("Retrying %s in %dms (Attempt %d)", action, delayMs, attempt));
    }

    public void logRecoveryInjection(ActionKey failedAction, ActionKey recoveryAction) {
        String msg = String.format("   %s↳%s Injecting recovery action: %s%s", CYAN, RESET, BOLD, recoveryAction, RESET);
        System.out.println(msg);
        appendToFile(String.format("Injecting recovery action %s for %s", recoveryAction, failedAction));
    }

    public void logDebug(String message) {
        if (debugMode) {
            String msg = String.format("   %s[DEBUG]%s %s", CYAN, RESET, message);
            System.out.println(msg);
        }
        appendToFile(String.format("[DEBUG] %s", message));
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

        appendToFile(String.format("Pipeline Finished - Result: %s (%s), Duration: %dms", 
                     (isSuccess ? "Success" : "Failed"), resultMessage, duration));
    }
}
