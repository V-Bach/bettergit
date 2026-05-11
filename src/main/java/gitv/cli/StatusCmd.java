package gitv.cli;

import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.suggestion.DecisionEngine;
import gitv.suggestion.DecisionResult;
import gitv.workflow.ExecutionPlan;
import gitv.workflow.PlanBuilder;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "status", description = "Show repository status and recommended actions.")
public class StatusCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return 1;
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

        PlanBuilder planBuilder = new PlanBuilder();
        ExecutionPlan plan = planBuilder.build(result);

        CliFormatter.printPlan(plan);
        CliFormatter.printReasoning(result);
        
        return 0;
    }
}
