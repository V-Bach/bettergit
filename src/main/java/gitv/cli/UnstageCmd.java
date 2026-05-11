package gitv.cli;

import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "unstage", description = "Unstage currently staged files, moving them back to working directory.")
public class UnstageCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return 1;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();
        
        if (!context.hasStagedChanges()) {
            System.out.println(Ansi.color("No prepared (staged) files to unstage.", Ansi.GRAY));
            return 0;
        }

        if (git.unstageAll()) {
            System.out.println(Ansi.colorBold("Success:", Ansi.GREEN) + " You prepared files for saving by mistake. Gitv moved them back to editing mode.");
            return 0;
        } else {
            System.out.println(Ansi.colorBold("Error:", Ansi.RED) + " Failed to unstage files.");
            return 1;
        }
    }
}
