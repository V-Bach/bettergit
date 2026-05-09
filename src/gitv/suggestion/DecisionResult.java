package gitv.suggestion;

import gitv.suggestion.rule.Goal;
import gitv.suggestion.rule.RuleResponse;
import gitv.workflow.ModuleIntent;

import java.util.List;

public class DecisionResult {
    private final Goal goal;
    private final List<ModuleIntent> intents;
    private final List<RuleResponse> appliedRules;
    private final List<gitv.workflow.Advisory> allAdvisories;
    private final gitv.workflow.ExecutionMode mode;
    private final java.util.Set<gitv.suggestion.rule.Signal> signals;

    public DecisionResult(Goal goal, List<ModuleIntent> intents, List<RuleResponse> appliedRules, List<gitv.workflow.Advisory> allAdvisories, gitv.workflow.ExecutionMode mode, java.util.Set<gitv.suggestion.rule.Signal> signals) {
        this.goal = goal;
        this.intents = intents;
        this.appliedRules = appliedRules;
        this.allAdvisories = allAdvisories;
        this.mode = mode != null ? mode : gitv.workflow.ExecutionMode.AUTO;
        this.signals = signals;
    }

    public Goal getGoal() { return goal; }
    public List<ModuleIntent> getIntents() { return intents; }
    public List<RuleResponse> getAppliedRules() { return appliedRules; }
    public List<gitv.workflow.Advisory> getAllAdvisories() { return allAdvisories; }
    public gitv.workflow.ExecutionMode getMode() { return mode; }
    public java.util.Set<gitv.suggestion.rule.Signal> getSignals() { return signals; }
}
