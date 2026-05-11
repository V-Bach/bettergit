package gitv.workflow.basic;

import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class PullRebaseWorkflow implements Workflow {
    private final GitService git;

    public PullRebaseWorkflow(GitService git) {
        this.git = git;
    }

    @Override
    public WorkflowResult execute(ExecutionContext context) {
        boolean success = git.pullRebase();
        if (success) {
            return new WorkflowResult(true, "Pulled from remote with rebase.");
        } else {
            return new WorkflowResult(false, "Merge conflict during rebase or pull failed.", null, FailureCategory.RECOVERABLE_ERROR);
        }
    }

    @Override
    public int getMaxRetries() {
        return 0;
    }
}
