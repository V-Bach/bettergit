package gitv.cli;

import gitv.engine.ActionType;
import gitv.engine.ExecutionEngine;
import gitv.suggestion.Suggestion;
import gitv.suggestion.SuggestionEngine;
import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowRegistry;
import gitv.workflow.WorkflowResult;
import gitv.workflow.basic.CommitWorkflow;
import gitv.workflow.basic.PullWorkflow;
import gitv.workflow.basic.PushWorkflow;
import gitv.workflow.composite.SyncWorkflow;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandRouter {
    private final Scanner scanner = new Scanner(System.in);

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

                System.out.println("Branch: " + context.getBranch());
                System.out.println("Changes: " + context.getChangedFiles() + " files");

                SuggestionEngine engine = new SuggestionEngine();
                Suggestion suggestion = engine.suggest(context);
                System.out.println(suggestion.getMessage());
                break;
            }
            case "go": {
                SuggestionEngine engine = new SuggestionEngine();
                GitService git = new GitService();
                if (!git.isGitRepository()) {
                    System.out.println("❌ Not a git repository");
                    break;
                }

                // Initialize execution system
                List<Workflow> workflows = Arrays.asList(
                        new CommitWorkflow(git),
                        new PushWorkflow(git),
                        new PullWorkflow(git),
                        new SyncWorkflow(git));
                WorkflowRegistry registry = new WorkflowRegistry(workflows);
                ExecutionEngine executionEngine = new ExecutionEngine(registry);

                ContextBuilder builder = new ContextBuilder();
                RepoContext context = builder.build();

                Suggestion suggestion = engine.suggest(context);
                System.out.println(suggestion.getMessage());

                List<ActionType> actions = suggestion.getActions();
                if (actions != null && !actions.isEmpty()
                        && !(actions.size() == 1 && actions.get(0) == ActionType.NONE)) {
                    boolean proceed = true;
                    if (suggestion.requiresConfirmation()) {
                        System.out.print(suggestion.getConfirmationMessage());
                        String answer = scanner.nextLine();
                        if (!answer.trim().equalsIgnoreCase("y")) {
                            proceed = false;
                            System.out.println("Aborted.");
                        }
                    }

                    if (proceed) {
                        WorkflowResult result = executionEngine.execute(actions, context);
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
}
