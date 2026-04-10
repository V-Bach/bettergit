package gitv.workflow.basic;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class PullWorkflow implements Workflow {
    private final GitService gitService;

    public PullWorkflow(GitService gitService) {
        this.gitService = gitService;
    }



    @Override
    public WorkflowResult execute(ExecutionContext context) {
        boolean success = gitService.pull();
        return new WorkflowResult(
                success, 
                success ? "Pull successful." : "Pull failed. Retrying might help.", 
                null, 
                success ? FailureCategory.NONE : FailureCategory.RECOVERABLE_ERROR
        );
    }
}
