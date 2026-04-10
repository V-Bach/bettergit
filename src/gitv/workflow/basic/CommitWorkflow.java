package gitv.workflow.basic;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.FailureType;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class CommitWorkflow implements Workflow {
    private final GitService gitService;

    public CommitWorkflow(GitService gitService) {
        this.gitService = gitService;
    }



    @Override
    public WorkflowResult execute(ExecutionContext context) {
        boolean success = gitService.commitAll();
        return new WorkflowResult(
                success, 
                success ? "Commit successful." : "Commit failed.", 
                false, 
                null, 
                success ? FailureType.NONE : FailureType.FATAL
        );
    }
}
