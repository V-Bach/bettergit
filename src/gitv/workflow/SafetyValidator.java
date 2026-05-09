package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;

public class SafetyValidator {

    public SafetyResult validate(ExecutionPlan plan, RepoContext context) {
        if (context != null && context.isLocked()) {
            return SafetyResult.failure("Repository is locked: " + context.getLockReason());
        }

        if (plan == null || plan.getSteps() == null) {
            return SafetyResult.safe(RiskLevel.NONE);
        }
        
        RepoContext simulatedContext = context;
        RiskLevel maxRisk = RiskLevel.NONE;

        for (ExecutionStep step : plan.getSteps()) {
            ActionKey action = step.getAction();

            // Risk Assessment Phase
            RiskLevel stepRisk = assessRisk(action);
            if (stepRisk.compareTo(maxRisk) > 0) {
                maxRisk = stepRisk;
            }

            // 1. Validation Phase
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
            if (action == ActionKey.ADD) {
                simulatedContext = simulatedContext.withStagedChanges(true);
            }else if (action == ActionKey.PULL) {
                simulatedContext = simulatedContext.withBehindRemote(false);
            } else if (action == ActionKey.COMMIT) {
                simulatedContext = simulatedContext.withStagedChanges(false);
                simulatedContext = simulatedContext.withAheadOfRemote(true);
            } else if (action == ActionKey.PUSH) {
                simulatedContext = simulatedContext.withAheadOfRemote(false);
                simulatedContext = simulatedContext.withUnpushedCommits(false);
            }
        }

        return SafetyResult.safe(maxRisk);
    }

    private RiskLevel assessRisk(ActionKey action) {
        if (action == ActionKey.PUSH || action == ActionKey.PULL_REBASE) {
            return RiskLevel.HIGH;
        } else if (action == ActionKey.COMMIT || action == ActionKey.PULL || action == ActionKey.STASH) {
            return RiskLevel.MEDIUM;
        } else if (action == ActionKey.ADD || action == ActionKey.NONE) {
            return RiskLevel.LOW;
        }
        return RiskLevel.NONE;
    }
}
