package gitv.suggestion.rule;

import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;
import gitv.workflow.Option;

import java.util.Collections;
import java.util.Set;

public class RuleResponse {
    private final Goal goal;
    private final Tier tier;
    private final int score;
    private final ModuleID module;
    private final Set<Option> options;
    private final Anchor anchor;
    private final String reason;

    public RuleResponse(Goal goal, Tier tier, int score, ModuleID module, Set<Option> options, Anchor anchor, String reason) {
        this.goal = goal;
        this.tier = tier;
        this.score = score;
        this.module = module;
        this.options = options != null ? options : Collections.emptySet();
        this.anchor = anchor;
        this.reason = reason;
    }

    public Goal getGoal() { return goal; }
    public Tier getTier() { return tier; }
    public int getScore() { return score; }
    public ModuleID getModule() { return module; }
    public Set<Option> getOptions() { return options; }
    public Anchor getAnchor() { return anchor; }
    public String getReason() { return reason; }
}
