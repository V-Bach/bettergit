package gitv.workflow.composite;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.FailureType;
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
            return new WorkflowResult(false, "Sync failed. Repository has active merge conflicts.", false, null, FailureType.FATAL);
        }

        boolean commitSuccess = gitService.commitAll();
        if (!commitSuccess) {
            return new WorkflowResult(false, "Sync failed during commit.", false, null, FailureType.FATAL);
        }
        boolean pushSuccess = gitService.push();
        if (pushSuccess) {
            return new WorkflowResult(true, "Sync successful.", false, null, FailureType.NONE);
        } else {
            return new WorkflowResult(false, "Sync failed during push. Remote may have changes.", true, ActionKey.PULL, FailureType.CONFLICT);
        }
    }
}
