package gitv.workflow.basic;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class PushWorkflow implements Workflow {
    private final GitService gitService;

    public PushWorkflow(GitService gitService) {
        this.gitService = gitService;
    }



    @Override
    public WorkflowResult execute(ExecutionContext context) {
        if (gitService.hasMergeConflicts()) {
            return new WorkflowResult(false, "Push failed. Repository has active merge conflicts.", null, FailureCategory.FATAL_ERROR);
        }

        boolean success = gitService.push();
        if (success) {
            return new WorkflowResult(true, "Push successful.");
        } else {
            return new WorkflowResult(false, "Push failed. Remote may have changes.", ActionKey.PULL, FailureCategory.RECOVERABLE_ERROR);
        }
    }
}
