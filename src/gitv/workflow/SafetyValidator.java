package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

public class SafetyValidator {

    public SafetyResult validate(ExecutionPlan plan, RepoContext context) {
        if (plan == null || plan.getSteps() == null) {
            return SafetyResult.safe();
        }

        for (ExecutionStep step : plan.getSteps()) {
            ActionKey action = step.getAction();

            if (action == ActionKey.COMMIT) {
                if (!context.hasStagedChanges()) {
                    return SafetyResult.failure("Cannot commit without staged changes.");
                }
            } else if (action == ActionKey.PUSH) {
                // Example check: if we were to define conflict state in RepoContext
                // if (context.hasConflicts()) {
                //    return SafetyResult.failure("Cannot push while repository is in conflict.");
                // }
                // For now, based on current RepoContext, if we push while behind, it's unsafe.
                if (context.isBehindRemote()) {
                    return SafetyResult.failure("Cannot push while behind remote.");
                }
            }
            // Add other invariant checks here as needed
        }

        return SafetyResult.safe();
    }
}
