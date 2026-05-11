package gitv.cli;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.ExecutionEngine;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowRegistry;
import gitv.workflow.WorkflowResult;
import gitv.workflow.basic.AddWorkflow;
import gitv.workflow.basic.CommitWorkflow;
import gitv.workflow.basic.PullRebaseWorkflow;
import gitv.workflow.basic.PullWorkflow;
import gitv.workflow.basic.PushWorkflow;
import gitv.workflow.basic.StashWorkflow;
import gitv.workflow.composite.SyncWorkflow;

public class EngineFactory {

    public static ExecutionEngine create() {
        GitService git = new GitService();
        WorkflowRegistry registry = new WorkflowRegistry();

        registry.register(ActionKey.COMMIT, new CommitWorkflow(git));
        registry.register(ActionKey.PUSH, new PushWorkflow(git));
        registry.register(ActionKey.PULL, new PullWorkflow(git));
        registry.register(ActionKey.PULL_REBASE, new PullRebaseWorkflow(git));
        registry.register(ActionKey.SYNC, new SyncWorkflow(git));
        registry.register(ActionKey.ADD, new AddWorkflow(git));
        registry.register(ActionKey.STASH, new StashWorkflow(git));

        registry.register(ActionKey.of("DEPLOY"), new Workflow() {
            @Override
            public WorkflowResult execute(ExecutionContext context) {
                return new WorkflowResult(true, "Deployed successfully!");
            }
        });

        registry.lock();

        return new ExecutionEngine(registry);
    }
}
