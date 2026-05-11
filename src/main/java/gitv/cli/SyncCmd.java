package gitv.cli;

import gitv.suggestion.rule.Goal;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "sync", description = "Synchronize local changes with the remote repository.")
public class SyncCmd implements Callable<Integer> {

    @Option(names = "--apply", description = "Apply the execution plan automatically")
    boolean apply;

    @Option(names = "--explain", description = "Explain the reasoning without applying")
    boolean explain;

    @Option(names = "--confirm", description = "Prompt before execution")
    boolean confirm;

    @Override
    public Integer call() {
        WorkflowRunner runner = new WorkflowRunner(EngineFactory.create());
        return runner.run(Goal.SYNCHRONIZE, apply, explain, confirm);
    }
}
