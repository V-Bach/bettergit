package gitv.workflow.basic;

import gitv.engine.ActionType;
import gitv.engine.ExecutionContext;
import gitv.engine.FailureType;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class PushWorkflow implements Workflow {
    private final GitService gitService;

    public PushWorkflow(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public ActionType getType() {
        return ActionType.PUSH;
    }

    @Override
    public WorkflowResult execute(ExecutionContext context) {
        if (gitService.hasMergeConflicts()) {
            return new WorkflowResult(false, "Push failed. Repository has active merge conflicts.", false, null, FailureType.FATAL);
        }

        boolean success = gitService.push();
        if (success) {
            return new WorkflowResult(true, "Push successful.", false, null, FailureType.NONE);
        } else {
            return new WorkflowResult(false, "Push failed. Remote may have changes.", true, ActionType.PULL, FailureType.CONFLICT);
        }
    }
}
