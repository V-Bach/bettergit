package gitv.cli;

import gitv.suggestion.DecisionResult;
import gitv.workflow.ExecutionPlan;
import gitv.workflow.ExecutionStep;
import gitv.workflow.SafetyResult;

import java.util.List;

public class CliFormatter {

    public static void printExplanation(DecisionResult result, ExecutionPlan plan, SafetyResult safetyResult) {
        System.out.println(Ansi.bold("\n--- Execution Explanation ---------------------------"));
        
        System.out.println("  " + Ansi.bold("Goal:") + " " + Ansi.colorBold(result.getGoal().toString(), Ansi.CYAN));
        
        System.out.println();
        System.out.println("  " + Ansi.bold("Signals Detected:"));
        if (result.getSignals() != null && !result.getSignals().isEmpty()) {
            for (gitv.suggestion.rule.Signal signal : result.getSignals()) {
                System.out.println("    " + Ansi.color("- ", Ansi.GRAY) + signal);
            }
        } else {
            System.out.println("    " + Ansi.color("- None", Ansi.GRAY));
        }

        System.out.println();
        System.out.println("  " + Ansi.bold("Plan:"));
        List<ExecutionStep> steps = plan.getSteps();
        if (steps == null || steps.isEmpty()) {
            System.out.println("    " + Ansi.color("- NONE (No action required)", Ansi.GRAY));
        } else {
            for (int i = 0; i < steps.size(); i++) {
                System.out.println("    " + Ansi.color((i + 1) + ".", Ansi.GRAY) + " " + Ansi.bold(steps.get(i).getAction().toString()));
            }
        }

        System.out.println();
        System.out.println("  " + Ansi.bold("Reasoning:"));
        if (result.getAppliedRules() != null && !result.getAppliedRules().isEmpty()) {
            for (gitv.suggestion.rule.RuleResponse rule : result.getAppliedRules()) {
                System.out.println("    " + Ansi.color("- ", Ansi.GRAY) + rule.getAdvisory().message());
            }
        } else {
            System.out.println("    " + Ansi.color("- No specific rules applied.", Ansi.GRAY));
        }

        System.out.println();
        System.out.println("  " + Ansi.bold("Risk Assessment:"));
        String riskColor = Ansi.GREEN;
        if (safetyResult.getRiskLevel() == gitv.workflow.RiskLevel.HIGH || safetyResult.getRiskLevel() == gitv.workflow.RiskLevel.CRITICAL) {
            riskColor = Ansi.RED;
        } else if (safetyResult.getRiskLevel() == gitv.workflow.RiskLevel.MEDIUM) {
            riskColor = Ansi.YELLOW;
        }
        System.out.println("    " + Ansi.color("Level: ", Ansi.GRAY) + Ansi.colorBold(safetyResult.getRiskLevel().toString(), riskColor));
        if (!safetyResult.isSafe()) {
            System.out.println("    " + Ansi.colorBold("BLOCKED: ", Ansi.RED) + safetyResult.getMessage());
        }
        System.out.println(Ansi.bold("-----------------------------------------------------\n"));
    }

    public static void printPlan(ExecutionPlan plan) {
        List<ExecutionStep> steps = plan.getSteps();
        
        if (steps == null || steps.isEmpty()) {
            System.out.println(Ansi.bold("Execution Plan:"));
            System.out.println("  " + Ansi.color("NONE (No action required)", Ansi.GRAY));
            return;
        }

        System.out.println(Ansi.bold("Execution Plan:"));
        for (int i = 0; i < steps.size(); i++) {
            ExecutionStep step = steps.get(i);
            System.out.println("  " + Ansi.colorBold("[ ] ", Ansi.CYAN) + Ansi.bold(step.getAction().toString()));
            List<String> reasons = step.getReasons();
            if (reasons != null && !reasons.isEmpty()) {
                for (String reason : reasons) {
                    System.out.println(Ansi.color("      ↳ " + reason, Ansi.GRAY));
                }
            }
            if (i < steps.size() - 1) {
                System.out.println();
            }
        }
    }

    public static void printReasoning(DecisionResult decisionResult) {
        if (decisionResult == null || decisionResult.getAppliedRules() == null || decisionResult.getAppliedRules().isEmpty()) {
            return;
        }
        System.out.println(Ansi.bold("\nReasoning ") + Ansi.color("(Goal: " + decisionResult.getGoal() + "):", Ansi.GRAY));
        for (gitv.suggestion.rule.RuleResponse rule : decisionResult.getAppliedRules()) {
            System.out.println("  " + Ansi.colorBold("• ", Ansi.BLUE) + rule.getAdvisory().message());
            System.out.println(Ansi.color("    [" + rule.getTier() + "] " + rule.getModule() + " (Score: " + rule.getScore() + ")", Ansi.GRAY));
        }
    }
}
