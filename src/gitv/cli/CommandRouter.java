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
            System.out.println("❌ Not a git repository");
            return;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        System.out.println("Unstaged Changes: " + context.hasUnstagedChanges());
        System.out.println("Staged Changes: " + context.hasStagedChanges());
        System.out.println("Unpushed Commits: " + context.hasUnpushedCommits());
        if (context.isLocked()) {
            System.out.println("🔒 Repository is LOCKED: " + context.getLockReason());
        }

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
            System.out.println("❌ Not a git repository");
            return;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        DecisionEngine engine = new DecisionEngine();
        DecisionResult result = engine.decide(context);

        List<gitv.workflow.Advisory> advisories = result.getAllAdvisories();
        if (advisories == null || advisories.isEmpty()) {
            System.out.println("✅ Repository is healthy. No issues detected.");
            return;
        }

        System.out.println("🏥 Gitv Doctor Report:\n");
        for (gitv.workflow.Advisory advisory : advisories) {
            System.out.println("- [" + advisory.severity() + "] " + advisory.message());
            if (advisory.actionableFix() != null && advisory.actionableFix() != ActionKey.NONE) {
                System.out.println("  💡 Suggested Fix: Run `gitv go` to execute " + advisory.actionableFix());
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
            System.out.println("❌ Not a git repository");
            return;
        }

        String currentHash = git.getHeadHash();
        gitv.engine.StateManager stateManager = new gitv.engine.StateManager(git.getRepoRoot());
        gitv.engine.ExecutionState recoveredState = null;
        
        if (stateManager.hasState()) {
            gitv.engine.ExecutionState state = stateManager.loadState();
            if (state != null && state.getPlannedActions() != null && state.getCompletedSteps().size() < state.getPlannedActions().size()) {
                if (currentHash.equals(state.getInitialHeadHash())) {
                    System.out.print("\n⚠️ Found an interrupted execution plan. Resume? (y/n) ");
                    String answer = scanner.nextLine();
                    if (answer.trim().equalsIgnoreCase("y")) {
                        recoveredState = state;
                    } else {
                        stateManager.clearState();
                    }
                } else {
                    System.out.println("⚠️ Repository state changed since last crash. Discarding old state.");
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
            System.out.println("\n[DRY RUN] Run with `--apply` to execute.");
            return;
        }

        List<ExecutionStep> steps = plan.getSteps();
        if (steps == null || steps.isEmpty() || steps.get(0).getAction() == ActionKey.NONE) {
            System.out.println("No actions required.");
            return;
        }

        if (plan.getMode() == gitv.workflow.ExecutionMode.INTERACTIVE) {
            System.out.println("\n⚠️  INTERACTIVE MODE: Gitv will yield terminal control for manual intervention.");
        } else if (plan.getMode() == gitv.workflow.ExecutionMode.GUARDED) {
            System.out.println("\n⚠️  GUARDED MODE: This plan mutates repository state and may halt midway.");
            System.out.print("Do you want to proceed? Type 'yes' to confirm: ");
            String answer = scanner.nextLine();
            if (!answer.trim().equalsIgnoreCase("yes")) {
                System.out.println("Aborted.");
                return;
            }
            context = builder.build();
        } else if (isConfirm) {
            System.out.print("\nDo you want to run the execution plan? (y/n) ");
            String answer = scanner.nextLine();
            if (!answer.trim().equalsIgnoreCase("y")) {
                System.out.println("Aborted.");
                return;
            }
            context = builder.build();
        }

        if (!safetyResult.isSafe()) {
            System.out.println("\n❌ Safety Guard: Cannot execute plan. Reason: " + safetyResult.getMessage());
            return;
        }

        String executionId = recoveredState != null && recoveredState.getExecutionId() != null 
                ? recoveredState.getExecutionId() 
                : java.util.UUID.randomUUID().toString();
        java.io.File logFile = new java.io.File(git.getRepoRoot(), ".git/gitv/execution.log");

        WorkflowResult result = executionEngine.execute(plan, context, executionId, logFile, stateManager, recoveredState, currentHash);
        if (result.isSuccess()) {
            System.out.println("✅ " + result.getMessage());
        } else {
            System.out.println("❌ Execution Failed: " + result.getMessage());
            System.out.println("   (Check .git/gitv/execution.log for full details)");
            
            System.out.println("\n🔄 Re-evaluating repository state for recovery guidance...");
            context = builder.build();
            DecisionResult recoveryDecision = engine.decide(context);
            
            if (recoveryDecision != null && !recoveryDecision.getAllAdvisories().isEmpty()) {
                System.out.println("\n💡 Suggested Fix (Goal: " + recoveryDecision.getGoal() + "):");
                for (gitv.workflow.Advisory advisory : recoveryDecision.getAllAdvisories()) {
                    System.out.println("- [" + advisory.severity() + "] " + advisory.message());
                    if (advisory.actionableFix() != null && advisory.actionableFix() != ActionKey.NONE) {
                        System.out.println("  Action: run `gitv go` to execute " + advisory.actionableFix());
                    }
                }
            } else {
                System.out.println("\nNo automatic recovery suggestions available.");
            }
        }
    }

    private void printExplanation(DecisionResult result, gitv.workflow.ExecutionPlan plan, SafetyResult safetyResult) {
        System.out.println("🔍 Execution Explanation\n");
        
        System.out.println("🎯 Goal: " + result.getGoal());
        
        System.out.println("\n📡 Signals Detected:");
        if (result.getSignals() != null && !result.getSignals().isEmpty()) {
            for (gitv.suggestion.rule.Signal signal : result.getSignals()) {
                System.out.println("  - " + signal);
            }
        } else {
            System.out.println("  - None");
        }

        System.out.println("\n📋 Plan:");
        printPlan(plan);

        System.out.println("🧠 Reasoning:");
        if (result.getAppliedRules() != null && !result.getAppliedRules().isEmpty()) {
            for (gitv.suggestion.rule.RuleResponse rule : result.getAppliedRules()) {
                System.out.println("  - " + rule.getAdvisory().message());
            }
        } else {
            System.out.println("  - No specific rules applied.");
        }

        System.out.println("\n⚠️ Risk Assessment:");
        System.out.println("  - Level: " + safetyResult.getRiskLevel());
        if (!safetyResult.isSafe()) {
            System.out.println("  - 🛑 BLOCKED: " + safetyResult.getMessage());
        }
    }

    private void printPlan(gitv.workflow.ExecutionPlan plan) {
        List<ExecutionStep> steps = plan.getSteps();
        
        if (steps == null || steps.isEmpty()) {
            System.out.println("Execution Plan:\n- NONE (No action required)");
            return;
        }

        System.out.println("Execution Plan:");
        for (int i = 0; i < steps.size(); i++) {
            ExecutionStep step = steps.get(i);
            System.out.println((i + 1) + ". " + step.getAction().toString());
            List<String> reasons = step.getReasons();
            if (reasons != null && !reasons.isEmpty()) {
                for (String reason : reasons) {
                    System.out.println("   - " + reason);
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
        System.out.println("\nReasoning (Goal: " + decisionResult.getGoal() + "):");
        for (gitv.suggestion.rule.RuleResponse rule : decisionResult.getAppliedRules()) {
            System.out.println("- [" + rule.getTier() + "] " + rule.getModule() + " (Score: " + rule.getScore() + ")");
            System.out.println("    * " + rule.getAdvisory().message());
        }
    }
}
