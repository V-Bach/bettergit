package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

public class SafetyValidator {

    public SafetyResult validate(ExecutionPlan plan, RepoContext context) {
        if (plan == null || plan.getSteps() == null) {
            return SafetyResult.safe();
        }

        RepoContext simulatedContext = context;

        for (ExecutionStep step : plan.getSteps()) {
            ActionKey action = step.getAction();

            // 1. Validation Phase against simulated state
            if (action == ActionKey.COMMIT) {
                if (!simulatedContext.hasStagedChanges()) {
                    return SafetyResult.failure("Cannot commit without staged changes.");
                }
            } else if (action == ActionKey.PUSH) {
                if (simulatedContext.isBehindRemote()) {
                    return SafetyResult.failure("Cannot push while behind remote.");
                }
            }
            // Add other invariant checks here as needed

            // 2. State Transition Phase
            if (action == ActionKey.PULL) {
                simulatedContext = simulatedContext.withBehindRemote(false);
            } else if (action == ActionKey.COMMIT) {
                simulatedContext = simulatedContext.withStagedChanges(false);
                simulatedContext = simulatedContext.withAheadOfRemote(true);
            } else if (action == ActionKey.PUSH) {
                simulatedContext = simulatedContext.withAheadOfRemote(false);
                simulatedContext = simulatedContext.withUnpushedCommits(false);
            }
        }

        return SafetyResult.safe();
    }
}
