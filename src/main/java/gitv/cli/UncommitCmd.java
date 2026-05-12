package gitv.cli;

import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "uncommit", description = "Safely undo the last commit without losing changes.")
public class UncommitCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return 1;
        }
        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        if (!context.hasUnpushedCommits()) {
            System.out.println(Ansi.colorBold("Safety Guard Blocked:", Ansi.RED)
                    + " You have no local unpushed saves to undo. We cannot undo pushed work because it modifies team history.");
            return 1;
        }

        if (git.isHeadMergeCommit()) {
            System.out.println(Ansi.colorBold("Safety Guard Blocked:", Ansi.RED)
                    + " Your last save is a merge commit. Safely undoing a merge requires manual intervention. Run `git reset --merge HEAD~1` if you are sure.");
            return 1;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println(Ansi.colorBold("\nWarning: GUARDED MODE: This will rewind your last save.", Ansi.YELLOW));
        System.out.print(Ansi.bold("Do you want to proceed? [y/N]: "));
        String answer = scanner.nextLine();

        if (!answer.trim().equalsIgnoreCase("y") && !answer.trim().equalsIgnoreCase("yes")) {
            System.out.println(Ansi.color("Aborted.", Ansi.GRAY));
            return 0;
        }

        if (git.uncommit()) {
            System.out.println(Ansi.colorBold("Success:", Ansi.GREEN)
                    + " Your last save has been reversed. The files are back in your staging area, and no code was lost.");
            return 0;
        } else {
            System.out.println(Ansi.colorBold("Error:", Ansi.RED) + " Failed to uncommit.");
            return 1;
        }
    }
}
