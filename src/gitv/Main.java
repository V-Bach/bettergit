package gitv;

import gitv.cli.CommandRouter;
import gitv.engine.ActionKey;
import gitv.engine.ExecutionContext;
import gitv.engine.ExecutionEngine;
import gitv.git.GitService;
import gitv.workflow.Workflow;
import gitv.workflow.WorkflowRegistry;
import gitv.workflow.WorkflowResult;
import gitv.workflow.basic.CommitWorkflow;
import gitv.workflow.basic.PullWorkflow;
import gitv.workflow.basic.PushWorkflow;
import gitv.workflow.composite.SyncWorkflow;

public class Main {
    public static void main(String[] args) {
        GitService git = new GitService();
        WorkflowRegistry registry = new WorkflowRegistry();

        registry.register(ActionKey.COMMIT, new CommitWorkflow(git));
        registry.register(ActionKey.PUSH, new PushWorkflow(git));
        registry.register(ActionKey.PULL, new PullWorkflow(git));
        registry.register(ActionKey.PULL_REBASE, new gitv.workflow.basic.PullRebaseWorkflow(git));
        registry.register(ActionKey.SYNC, new SyncWorkflow(git));
        registry.register(ActionKey.ADD, new gitv.workflow.basic.AddWorkflow(git));
        registry.register(ActionKey.STASH, new gitv.workflow.basic.StashWorkflow(git));

        registry.register(ActionKey.of("DEPLOY"), new Workflow() {
            @Override
            public WorkflowResult execute(ExecutionContext context) {
                return new WorkflowResult(true, "Deployed successfully!");
            }
        });

        registry.lock();

        ExecutionEngine engine = new ExecutionEngine(registry);

        CommandRouter router = new CommandRouter(engine);
        router.route(args);
    }
}
