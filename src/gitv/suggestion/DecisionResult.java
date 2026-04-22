package gitv.suggestion;

import gitv.suggestion.rule.Goal;
import gitv.suggestion.rule.RuleResponse;
import gitv.workflow.ModuleIntent;

import java.util.List;

public class DecisionResult {
    private final Goal goal;
    private final List<ModuleIntent> intents;
    private final List<RuleResponse> appliedRules;

    public DecisionResult(Goal goal, List<ModuleIntent> intents, List<RuleResponse> appliedRules) {
        this.goal = goal;
        this.intents = intents;
        this.appliedRules = appliedRules;
    }

    public Goal getGoal() { return goal; }
    public List<ModuleIntent> getIntents() { return intents; }
    public List<RuleResponse> getAppliedRules() { return appliedRules; }
}
