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
                GitService git = new GitService();
                if (!git.isGitRepository()) {
                    System.out.println("❌ Not a git repository");
                    break;
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
                break;
            }
            case "go": {
                boolean isDryRun = args.length > 1 && args[1].equals("--dry");
                boolean isConfirm = args.length > 1 && args[1].equals("--confirm");

                DecisionEngine engine = new DecisionEngine();
                GitService git = new GitService();
                if (!git.isGitRepository()) {
                    System.out.println("❌ Not a git repository");
                    break;
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
                    List<ExecutionStep> steps = recoveredState.getPlannedActions().stream()
                        .map(key -> new ExecutionStep(key, Collections.singletonList("Recovered from interrupted plan")))
                        .collect(Collectors.toList());
                    plan = new gitv.workflow.ExecutionPlan(steps, gitv.workflow.ExecutionMode.AUTO);
                } else {
                    decisionResult = engine.decide(context);
                    gitv.workflow.PlanBuilder planBuilder = new gitv.workflow.PlanBuilder();
                    plan = planBuilder.build(decisionResult);
                }

                printPlan(plan);
                printReasoning(decisionResult);

                List<ExecutionStep> steps = plan.getSteps();
                if (steps == null || steps.isEmpty() || steps.get(0).getAction() == ActionKey.NONE) {
                    break;
                }

                if (isDryRun) {
                    System.out.println("\n[DRY RUN] No actions executed.");
                    break;
                }

                if (plan.getMode() == gitv.workflow.ExecutionMode.INTERACTIVE) {
                    System.out.println("\n⚠️  INTERACTIVE MODE: Gitv will yield terminal control for manual intervention.");
                } else if (plan.getMode() == gitv.workflow.ExecutionMode.GUARDED) {
                    System.out.println("\n⚠️  GUARDED MODE: This plan mutates repository state and may halt midway.");
                    System.out.print("Do you want to proceed? Type 'yes' to confirm: ");
                    String answer = scanner.nextLine();
                    if (!answer.trim().equalsIgnoreCase("yes")) {
                        System.out.println("Aborted.");
                        break;
                    }
                    context = builder.build();
                } else if (isConfirm) {
                    System.out.print("\nDo you want to run the execution plan? (y/n) ");
                    String answer = scanner.nextLine();
                    if (!answer.trim().equalsIgnoreCase("y")) {
                        System.out.println("Aborted.");
                        break;
                    }
                    context = builder.build();
                }

                SafetyValidator safetyValidator = new SafetyValidator();
                SafetyResult safetyResult = safetyValidator.validate(plan, context);

                if (!safetyResult.isSafe()) {
                    System.out.println("\n❌ Safety Guard: Cannot execute plan. Reason: " + safetyResult.getMessage());
                    break;
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
                    
                    if (recoveryDecision != null && !recoveryDecision.getAppliedRules().isEmpty()) {
                        System.out.println("\n💡 Suggested Fix (Goal: " + recoveryDecision.getGoal() + "):");
                        for (gitv.suggestion.rule.RuleResponse rule : recoveryDecision.getAppliedRules()) {
                            gitv.workflow.Advisory advisory = rule.getAdvisory();
                            System.out.println("- [" + advisory.severity() + "] " + advisory.message());
                            if (advisory.actionableFix() != null) {
                                System.out.println("  Action: run `gitv go` to execute " + advisory.actionableFix());
                            }
                        }
                    } else {
                        System.out.println("\nNo automatic recovery suggestions available.");
                    }
                }
                break;
            }
            default:
                System.out.println("Unknown command");
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
