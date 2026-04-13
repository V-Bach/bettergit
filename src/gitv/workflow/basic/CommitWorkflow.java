package gitv.workflow.basic;

import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
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
                null,
                success ? FailureCategory.NONE : FailureCategory.FATAL_ERROR);
    }
}
