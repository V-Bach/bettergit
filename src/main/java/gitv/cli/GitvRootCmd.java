package gitv.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "gitv", mixinStandardHelpOptions = true, version = "1.0.0",
         description = "Autonomous Git workflow engine.",
         subcommands = {
             SyncCmd.class,
             CommitCmd.class,
             DoctorCmd.class,
             StatusCmd.class,
             GoCmd.class,
             UnstageCmd.class,
             UncommitCmd.class
         })
public class GitvRootCmd implements Callable<Integer> {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GitvRootCmd()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        // Fallback if user just runs `gitv` without subcommands
        System.out.println("Use `gitv --help` for available commands.");
        return 0;
    }
}
