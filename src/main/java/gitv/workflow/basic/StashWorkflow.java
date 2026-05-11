package gitv.workflow.basic;

import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class StashWorkflow implements Workflow {
    private final GitService git;

    public StashWorkflow(GitService git) {
        this.git = git;
    }

    @Override
    public WorkflowResult execute(ExecutionContext context) {
        boolean success = git.stash();
        if (success) {
            return new WorkflowResult(true, "Changes stashed successfully.");
        } else {
            return new WorkflowResult(false, "Failed to stash changes.", null, FailureCategory.RECOVERABLE_ERROR);
        }
    }

    @Override
    public int getMaxRetries() {
        return 0;
    }
}
