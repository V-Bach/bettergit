package gitv.cli;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionEngine;
import gitv.suggestion.DecisionEngine;
import gitv.suggestion.ScoredAction;
import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.workflow.WorkflowResult;

import java.util.Collections;
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
                ScoredAction action = engine.decide(context);
                
                printExplanation(action);
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

                ScoredAction action = engine.decide(context);
                printExplanation(action);

                if (action.getType() != ActionKey.NONE) {
                    System.out.print("Do you want to run the recommended command? (y/n) ");
                    String answer = scanner.nextLine();
                    if (!answer.trim().equalsIgnoreCase("y")) {
                        System.out.println("Aborted.");
                    } else {
                        WorkflowResult result = executionEngine.execute(Collections.singletonList(action.getType()), context);
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

    private void printExplanation(ScoredAction action) {
        System.out.println("Recommended: " + action.getType().toString());
        System.out.println("Score: " + action.getScore());
        System.out.println("Reasons:");
        for (String reason : action.getReasons()) {
            System.out.println("* " + reason);
        }
    }
}
