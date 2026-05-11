package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.suggestion.DecisionResult;
import gitv.suggestion.rule.RuleResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlanBuilder {

    public ExecutionPlan build(DecisionResult decision) {
        if (decision == null || decision.getIntents().isEmpty()) {
            return new ExecutionPlan(Collections.emptyList(), gitv.workflow.ExecutionMode.AUTO);
        }

        List<ExecutionStep> steps = new ArrayList<>();

        for (ModuleIntent intent : decision.getIntents()) {
            if (intent.getMode() == gitv.workflow.ExecutionMode.AUTO && intent.isMutative()) {
                throw new IllegalStateException("DecisionEngine produced invalid ExecutionMode for workflow: AUTO mode cannot be mutative.");
            }

            List<String> reasons = decision.getAppliedRules().stream()
                    .filter(r -> r.getModule() == intent.getId())
                    .map(r -> r.getAdvisory().message())
                    .collect(Collectors.toList());

            switch (intent.getId()) {
                case STAGE:
                    steps.add(new ExecutionStep(ActionKey.ADD, reasons));
                    break;
                case COMMIT:
                    steps.add(new ExecutionStep(ActionKey.COMMIT, reasons));
                    break;
                case PULL:
                    steps.add(new ExecutionStep(ActionKey.PULL, reasons));
                    break;
                case PULL_REBASE:
                    steps.add(new ExecutionStep(ActionKey.PULL_REBASE, reasons));
                    break;
                case PUSH:
                    steps.add(new ExecutionStep(ActionKey.PUSH, reasons));
                    break;
                case STASH:
                    steps.add(new ExecutionStep(ActionKey.STASH, reasons));
                    break;
                case NONE:
                    break;
            }
        }

        return new ExecutionPlan(steps, decision.getMode());
    }
}
