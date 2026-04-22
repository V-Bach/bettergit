package gitv.workflow.basic;

import gitv.engine.ExecutionContext;
import gitv.engine.FailureCategory;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowResult;

public class AddWorkflow implements Workflow {
    private final GitService gitService;

    public AddWorkflow(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public WorkflowResult execute(ExecutionContext context) {
        // Assume GitService has addAll method, or we can just use runCommand inside GitService.
        // Let's call it via gitService.addAll()
        boolean success = gitService.addAll();
        return new WorkflowResult(
                success,
                success ? "Add successful." : "Add failed.",
                null,
                success ? FailureCategory.NONE : FailureCategory.FATAL_ERROR);
    }
}
