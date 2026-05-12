package gitv.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

import java.util.concurrent.Callable;

@Command(name = "gitv", mixinStandardHelpOptions = true, version = "0.9.0-rc1",
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

    public static boolean isVerbose = false;

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output with stack traces", scope = ScopeType.INHERIT)
    public void setVerbose(boolean verbose) {
        GitvRootCmd.isVerbose = verbose;
    }

    public static void main(String[] args) {
        // Global uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println(Ansi.colorBold("\n[FATAL] Gitv encountered an unexpected error:", Ansi.RED));
            System.err.println(Ansi.color(throwable.getMessage() != null ? throwable.getMessage() : throwable.toString(), Ansi.RED));
            if (isVerbose) {
                throwable.printStackTrace(System.err);
            } else {
                System.err.println(Ansi.color("Run with --verbose for full trace.", Ansi.GRAY));
            }
            System.exit(1);
        });

        CommandLine cmd = new CommandLine(new GitvRootCmd());

        // Picocli execution exception handler (for logic errors inside subcommands)
        cmd.setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
            System.err.println(Ansi.colorBold("\n[ERROR] Execution failed:", Ansi.RED));
            System.err.println(Ansi.color(ex.getMessage() != null ? ex.getMessage() : ex.toString(), Ansi.RED));
            if (isVerbose) {
                ex.printStackTrace(System.err);
            } else {
                System.err.println(Ansi.color("Run with --verbose for full trace.", Ansi.GRAY));
            }
            return 1; // exit code 1
        });

        // Picocli parameter exception handler (for bad flags or syntax)
        cmd.setParameterExceptionHandler((ex, argsArray) -> {
            System.err.println(Ansi.colorBold("\n[ERROR] Invalid input: " + ex.getMessage(), Ansi.RED));
            System.err.println(Ansi.color("Use `gitv --help` or `gitv <command> --help` for usage details.", Ansi.GRAY));
            return 2; // exit code 2
        });

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        // Fallback if user just runs `gitv` without subcommands
        CommandLine.usage(this, System.out);
        return 0;
    }
}
