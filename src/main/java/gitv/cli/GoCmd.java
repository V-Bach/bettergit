package gitv.cli;

import gitv.suggestion.rule.Goal;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "go", description = "Execute the default workflow based on repository state.")
public class GoCmd implements Callable<Integer> {

    @Option(names = "--dry", description = "Dry run (do not apply)")
    boolean dry;

    @Option(names = "--apply", description = "Apply the execution plan automatically (default for go)")
    boolean apply;

    @Option(names = "--explain", description = "Explain the reasoning without applying")
    boolean explain;

    @Option(names = "--confirm", description = "Prompt before execution")
    boolean confirm;

    @Override
    public Integer call() {
        boolean isApply = apply || !dry;
        WorkflowRunner runner = new WorkflowRunner(EngineFactory.create());
        return runner.run(Goal.NONE, isApply, explain, confirm);
    }
}
