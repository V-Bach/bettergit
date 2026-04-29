package gitv.suggestion;

import gitv.suggestion.rule.Goal;
import gitv.suggestion.rule.RuleResponse;
import gitv.workflow.ModuleIntent;

import java.util.List;

public class DecisionResult {
    private final Goal goal;
    private final List<ModuleIntent> intents;
    private final List<RuleResponse> appliedRules;
    private final gitv.workflow.ExecutionMode mode;

    public DecisionResult(Goal goal, List<ModuleIntent> intents, List<RuleResponse> appliedRules, gitv.workflow.ExecutionMode mode) {
        this.goal = goal;
        this.intents = intents;
        this.appliedRules = appliedRules;
        this.mode = mode != null ? mode : gitv.workflow.ExecutionMode.AUTO;
    }

    public Goal getGoal() { return goal; }
    public List<ModuleIntent> getIntents() { return intents; }
    public List<RuleResponse> getAppliedRules() { return appliedRules; }
    public gitv.workflow.ExecutionMode getMode() { return mode; }
}
