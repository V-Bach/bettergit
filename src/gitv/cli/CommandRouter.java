package gitv.cli;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionEngine;
import gitv.suggestion.DecisionEngine;
import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.workflow.WorkflowResult;
import gitv.suggestion.DecisionResult;
import gitv.workflow.ExecutionStep;
import gitv.workflow.SafetyResult;
import gitv.workflow.SafetyValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

public class CommandRouter {
    private final Scanner scanner = new Scanner(System.in);
    private final ExecutionEngine executionEngine;

    public CommandRouter(ExecutionEngine executionEngine) {
        this.executionEngine = executionEngine;
    }

    public void route(String[] args) {
        if (args.length == 0) {
            System.out.println("No command");
            return;
        }
        String command = args[0];

        switch (command) {
            case "status": {
                handleStatus();
                break;
            }
            case "doctor": {
                handleDoctor();
                break;
            }
            case "sync":
            case "commit":
            case "go": {
                handleWorkflow(args);
                break;
            }
            default:
                System.out.println("Unknown command");
        }
    }

    private void handleStatus() {
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        System.out.println(Ansi.bold("Repository Status:"));
        System.out.println("  Unstaged Changes: " + (context.hasUnstagedChanges() ? Ansi.color("Yes", Ansi.YELLOW) : Ansi.color("No", Ansi.GREEN)));
        System.out.println("  Staged Changes:   " + (context.hasStagedChanges() ? Ansi.color("Yes", Ansi.CYAN) : Ansi.color("No", Ansi.GRAY)));
        System.out.println("  Unpushed Commits: " + (context.hasUnpushedCommits() ? Ansi.color("Yes", Ansi.BLUE) : Ansi.color("No", Ansi.GRAY)));
        if (context.isLocked()) {
            System.out.println(Ansi.colorBold("Repository is LOCKED: " + context.getLockReason(), Ansi.RED));
        }
        System.out.println();

        DecisionEngine engine = new DecisionEngine();
        DecisionResult result = engine.decide(context);
        
        gitv.workflow.PlanBuilder planBuilder = new gitv.workflow.PlanBuilder();
        gitv.workflow.ExecutionPlan plan = planBuilder.build(result);

        printPlan(plan);
        printReasoning(result);
    }

    private void handleDoctor() {
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        DecisionEngine engine = new DecisionEngine();
        DecisionResult result = engine.decide(context);

        List<gitv.workflow.Advisory> advisories = result.getAllAdvisories();
        if (advisories == null || advisories.isEmpty()) {
            System.out.println(Ansi.colorBold("Repository is healthy. No issues detected.", Ansi.GREEN));
            return;
        }

        System.out.println(Ansi.bold("Gitv Doctor Report:"));
        System.out.println(Ansi.color("======================", Ansi.GRAY));
        for (gitv.workflow.Advisory advisory : advisories) {
            String sevColor = advisory.severity() == gitv.workflow.Severity.DANGER ? Ansi.RED : Ansi.YELLOW;
            System.out.println(Ansi.colorBold("- [" + advisory.severity() + "] ", sevColor) + advisory.message());
            if (advisory.actionableFix() != null && advisory.actionableFix() != ActionKey.NONE) {
                System.out.println(Ansi.color("  Suggested Fix: Run `gitv go` to execute ", Ansi.CYAN) + Ansi.bold(advisory.actionableFix().toString()));
            }
            System.out.println();
        }
    }

    private void handleWorkflow(String[] args) {
        String command = args[0];
        boolean isApply = false;
        boolean isExplain = false;
        boolean isConfirm = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--apply") || args[i].equals("--dry") && args[0].equals("go")) isApply = true; // For backwards compatibility with `go --dry`
            if (args[i].equals("--explain")) isExplain = true;
            if (args[i].equals("--confirm")) isConfirm = true;
        }

        // For `go` backward compatibility, default is apply unless --dry is specified
        if (command.equals("go")) {
            boolean hasDry = false;
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("--dry")) hasDry = true;
            }
            isApply = !hasDry;
        }

        DecisionEngine engine = new DecisionEngine();
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return;
        }

        String currentHash = git.getHeadHash();
        gitv.engine.StateManager stateManager = new gitv.engine.StateManager(git.getRepoRoot());
        gitv.engine.ExecutionState recoveredState = null;
        
        if (stateManager.hasState()) {
            gitv.engine.ExecutionState state = stateManager.loadState();
            if (state != null && state.getPlannedActions() != null && state.getCompletedSteps().size() < state.getPlannedActions().size()) {
                if (currentHash.equals(state.getInitialHeadHash())) {
                    System.out.print(Ansi.colorBold("\nWarning: Found an interrupted execution plan. Resume? [y/N]: ", Ansi.YELLOW));
                    String answer = scanner.nextLine();
                    if (answer.trim().equalsIgnoreCase("y")) {
                        recoveredState = state;
                    } else {
                        stateManager.clearState();
                    }
                } else {
                    System.out.println(Ansi.color("Warning: Repository state changed since last crash. Discarding old state.", Ansi.YELLOW));
                    stateManager.clearState();
                }
            } else {
                stateManager.clearState();
            }
        }

        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        gitv.workflow.ExecutionPlan plan;
        DecisionResult decisionResult = null;

        if (recoveredState != null) {
            final List<ActionKey> completed = recoveredState.getCompletedSteps();
            List<ExecutionStep> steps = recoveredState.getPlannedActions().stream()
                .filter(key -> !completed.contains(key))
                .map(key -> new ExecutionStep(key, Collections.singletonList("Recovered from interrupted plan")))
                .collect(Collectors.toList());
            plan = new gitv.workflow.ExecutionPlan(steps, gitv.workflow.ExecutionMode.AUTO);
            
            // Dummy decision result for explain mode if recovering
            decisionResult = new DecisionResult(gitv.suggestion.rule.Goal.NONE, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), gitv.workflow.ExecutionMode.AUTO, Collections.emptySet());
        } else {
            gitv.suggestion.rule.Goal targetGoal = null;
            if (command.equals("commit")) {
                targetGoal = gitv.suggestion.rule.Goal.COMMIT_LOCAL;
            } else if (command.equals("sync")) {
                targetGoal = gitv.suggestion.rule.Goal.SYNCHRONIZE;
            }

            decisionResult = engine.decide(context, targetGoal);
            gitv.workflow.PlanBuilder planBuilder = new gitv.workflow.PlanBuilder();
            plan = planBuilder.build(decisionResult);
        }

        SafetyValidator safetyValidator = new SafetyValidator();
        SafetyResult safetyResult = safetyValidator.validate(plan, context);

        if (isExplain) {
            printExplanation(decisionResult, plan, safetyResult);
            return;
        }

        if (!isApply) {
            printPlan(plan);
            System.out.println(Ansi.color("\n[DRY RUN] Run with `--apply` to execute.", Ansi.YELLOW));
            return;
        }

        List<ExecutionStep> steps = plan.getSteps();
        if (steps == null || steps.isEmpty() || steps.get(0).getAction() == ActionKey.NONE) {
            System.out.println(Ansi.color("No actions required.", Ansi.GRAY));
            return;
        }

        if (plan.getMode() == gitv.workflow.ExecutionMode.INTERACTIVE) {
            System.out.println(Ansi.colorBold("\nWarning: INTERACTIVE MODE: Gitv will yield terminal control for manual intervention.", Ansi.YELLOW));
        } else if (plan.getMode() == gitv.workflow.ExecutionMode.GUARDED) {
            System.out.println(Ansi.colorBold("\nWarning: GUARDED MODE: This plan mutates repository state and may halt midway.", Ansi.YELLOW));
            System.out.print(Ansi.bold("Do you want to proceed? [y/N]: "));
            String answer = scanner.nextLine();
            if (!answer.trim().equalsIgnoreCase("y") && !answer.trim().equalsIgnoreCase("yes")) {
                System.out.println(Ansi.color("Aborted.", Ansi.GRAY));
                return;
            }
            context = builder.build();
        } else if (isConfirm) {
            System.out.print(Ansi.bold("\nDo you want to run the execution plan? [y/N]: "));
            String answer = scanner.nextLine();
            if (!answer.trim().equalsIgnoreCase("y") && !answer.trim().equalsIgnoreCase("yes")) {
                System.out.println(Ansi.color("Aborted.", Ansi.GRAY));
                return;
            }
            context = builder.build();
        }

        if (!safetyResult.isSafe()) {
            System.out.println(Ansi.colorBold("\nSafety Guard Blocked: Cannot execute plan. Reason: ", Ansi.RED) + safetyResult.getMessage());
            return;
        }

        String executionId = recoveredState != null && recoveredState.getExecutionId() != null 
                ? recoveredState.getExecutionId() 
                : java.util.UUID.randomUUID().toString();
        java.io.File logFile = new java.io.File(git.getRepoRoot(), ".git/gitv/execution.log");

        WorkflowResult result = executionEngine.execute(plan, context, executionId, logFile, stateManager, recoveredState, currentHash);
        if (result.isSuccess()) {
            System.out.println(Ansi.colorBold("Success: " + result.getMessage(), Ansi.GREEN));
        } else {
            System.out.println(Ansi.colorBold("Execution Failed: ", Ansi.RED) + result.getMessage());
            System.out.println(Ansi.color("   (Check .git/gitv/execution.log for full details)", Ansi.GRAY));
            
            System.out.println(Ansi.color("\nRe-evaluating repository state for recovery guidance...", Ansi.CYAN));
            context = builder.build();
            DecisionResult recoveryDecision = engine.decide(context);
            
            if (recoveryDecision != null && !recoveryDecision.getAllAdvisories().isEmpty()) {
                System.out.println(Ansi.bold("\nSuggested Fix (Goal: " + recoveryDecision.getGoal() + "):"));
                for (gitv.workflow.Advisory advisory : recoveryDecision.getAllAdvisories()) {
                    System.out.println("- [" + advisory.severity() + "] " + advisory.message());
                    if (advisory.actionableFix() != null && advisory.actionableFix() != ActionKey.NONE) {
                        System.out.println(Ansi.color("  Action: run `gitv go` to execute ", Ansi.CYAN) + Ansi.bold(advisory.actionableFix().toString()));
                    }
                }
            } else {
                System.out.println(Ansi.color("\nNo automatic recovery suggestions available.", Ansi.GRAY));
            }
        }
    }

    private void printExplanation(DecisionResult result, gitv.workflow.ExecutionPlan plan, SafetyResult safetyResult) {
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

    private void printPlan(gitv.workflow.ExecutionPlan plan) {
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

    private void printReasoning(DecisionResult decisionResult) {
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
