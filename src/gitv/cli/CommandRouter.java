package gitv.cli;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionEngine;
import gitv.suggestion.DecisionEngine;
import gitv.suggestion.ScoredAction;
import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.workflow.WorkflowResult;
import gitv.suggestion.DecisionResult;
import gitv.workflow.ExecutionStep;

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
                gitv.workflow.ExecutionPlan plan = planBuilder.build(result, context);

                printPlan(plan);
                printAlternatives(result.getAlternatives());
                break;
            }
            case "go": {
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
                gitv.workflow.ExecutionPlan plan = planBuilder.build(decisionResult, context);

                printPlan(plan);
                printAlternatives(decisionResult.getAlternatives());

                List<ExecutionStep> steps = plan.getSteps();
                if (steps != null && !steps.isEmpty()) {
                    System.out.print("\nDo you want to run the execution plan? (y/n) ");
                    String answer = scanner.nextLine();
                    if (!answer.trim().equalsIgnoreCase("y")) {
                        System.out.println("Aborted.");
                    } else {
                        List<ActionKey> extractActionKeys = steps.stream().map(ExecutionStep::getAction).collect(Collectors.toList());
                        WorkflowResult result = executionEngine.execute(extractActionKeys, context);
                        if (result.isSuccess()) {
                            System.out.println("✅ " + result.getMessage());
                        } else {
                            System.out.println("❌ " + result.getMessage());
                        }
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

    private void printAlternatives(List<ScoredAction> alternatives) {
        if (alternatives == null || alternatives.isEmpty()) {
            return;
        }
        System.out.println("\nAlternatives:");
        for (ScoredAction alt : alternatives) {
            String status = alt.isBlocked() ? "(blocked)" : "(score: " + alt.getScore() + ")";
            System.out.println("- " + alt.getType().toString() + " " + status);
            for (String reason : alt.getReasons()) {
                System.out.println("    * " + reason);
            }
        }
    }
}
