package gitv.workflow.composite;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class SyncWorkflow implements Workflow {
    private final GitService gitService;

    public SyncWorkflow(GitService gitService) {
        this.gitService = gitService;
    }



    @Override
    public WorkflowResult execute(ExecutionContext context) {
        if (gitService.hasMergeConflicts()) {
            return new WorkflowResult(false, "Sync failed. Repository has active merge conflicts.", null, FailureCategory.FATAL_ERROR);
        }

        boolean commitSuccess = gitService.commitAll();
        if (!commitSuccess) {
            return new WorkflowResult(false, "Sync failed during commit.", null, FailureCategory.FATAL_ERROR);
        }
        boolean pushSuccess = gitService.push();
        if (pushSuccess) {
            return new WorkflowResult(true, "Sync successful.");
        } else {
            return new WorkflowResult(false, "Sync failed during push. Remote may have changes.", ActionKey.PULL, FailureCategory.RECOVERABLE_ERROR);
        }
    }
}
