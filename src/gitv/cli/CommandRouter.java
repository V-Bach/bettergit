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

                ContextBuilder builder = new ContextBuilder();
                RepoContext context = builder.build();

                DecisionResult decisionResult = engine.decide(context);
                gitv.workflow.PlanBuilder planBuilder = new gitv.workflow.PlanBuilder();
                gitv.workflow.ExecutionPlan plan = planBuilder.build(decisionResult);

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

                if (isConfirm) {
                    System.out.print("\nDo you want to run the execution plan? (y/n) ");
                    String answer = scanner.nextLine();
                    if (!answer.trim().equalsIgnoreCase("y")) {
                        System.out.println("Aborted.");
                        break;
                    }
                    // Re-fetch context to prevent state drift
                    context = builder.build();
                }

                SafetyValidator safetyValidator = new SafetyValidator();
                SafetyResult safetyResult = safetyValidator.validate(plan, context);

                if (!safetyResult.isSafe()) {
                    System.out.println("\n❌ Safety Guard: Cannot execute plan. Reason: " + safetyResult.getMessage());
                    break;
                }

                WorkflowResult result = executionEngine.execute(plan, context);
                if (result.isSuccess()) {
                    System.out.println("✅ " + result.getMessage());
                } else {
                    System.out.println("❌ " + result.getMessage());
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
