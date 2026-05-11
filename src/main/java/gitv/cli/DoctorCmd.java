package gitv.cli;

import gitv.engine.ActionKey;
import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.suggestion.DecisionEngine;
import gitv.suggestion.DecisionResult;
import gitv.workflow.Advisory;
import gitv.workflow.Severity;
import picocli.CommandLine.Command;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "doctor", description = "Diagnose repository issues and suggest fixes.")
public class DoctorCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return 1;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        DecisionEngine engine = new DecisionEngine();
        DecisionResult result = engine.decide(context);

        List<Advisory> advisories = result.getAllAdvisories();
        if (advisories == null || advisories.isEmpty()) {
            System.out.println(Ansi.colorBold("Repository is healthy. No issues detected.", Ansi.GREEN));
            return 0;
        }

        System.out.println(Ansi.bold("Gitv Doctor Report:"));
        System.out.println(Ansi.color("======================", Ansi.GRAY));
        for (Advisory advisory : advisories) {
            String sevColor = advisory.severity() == Severity.DANGER ? Ansi.RED : Ansi.YELLOW;
            System.out.println(Ansi.colorBold("- [" + advisory.severity() + "] ", sevColor) + advisory.message());
            if (advisory.actionableFix() != null && advisory.actionableFix() != ActionKey.NONE) {
                System.out.println(Ansi.color("  Suggested Fix: Run `gitv go` to execute ", Ansi.CYAN) + Ansi.bold(advisory.actionableFix().toString()));
            }
            System.out.println();
        }

        // Educational Undo Guidance
        if (git.isHeadMergeCommit()) {
            System.out.println(Ansi.color("Guide: Gitv detects you are at a merge commit. If you want to cancel the merge, run: ", Ansi.YELLOW) + Ansi.bold("git merge --abort"));
            System.out.println();
        }
        if (context.hasUnpushedCommits()) {
            System.out.println(Ansi.color("Guide: Gitv detects you have local saves. If you want to safely undo the last save, run: ", Ansi.CYAN) + Ansi.bold("gitv uncommit"));
            System.out.println();
        }
        return 0;
    }
}
