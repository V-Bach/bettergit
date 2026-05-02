package gitv.suggestion.rule;

import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;
import gitv.workflow.Option;
import gitv.workflow.Advisory;

import java.util.Collections;
import java.util.Set;

public class RuleResponse implements Comparable<RuleResponse> {
    private final Goal goal;
    private final Tier tier;
    private final int score;
    private final ModuleID module;
    private final Set<Option> options;
    private final Anchor anchor;
    private final Advisory advisory;
    private final gitv.workflow.ExecutionMode mode;
    private final boolean isMutative;

    public RuleResponse(Goal goal, Tier tier, int score, ModuleID module, Set<Option> options, Anchor anchor, Advisory advisory, gitv.workflow.ExecutionMode mode, boolean isMutative) {
        this.goal = goal;
        this.tier = tier;
        this.score = score;
        this.module = module;
        this.options = options != null ? options : Collections.emptySet();
        this.anchor = anchor;
        this.advisory = advisory;
        this.mode = mode != null ? mode : gitv.workflow.ExecutionMode.AUTO;
        this.isMutative = isMutative;
    }

    public Goal getGoal() { return goal; }
    public Tier getTier() { return tier; }
    public int getScore() { return score; }
    public ModuleID getModule() { return module; }
    public Set<Option> getOptions() { return options; }
    public Anchor getAnchor() { return anchor; }
    public Advisory getAdvisory() { return advisory; }
    public gitv.workflow.ExecutionMode getMode() { return mode; }
    public boolean isMutative() { return isMutative; }

    @Override
    public int compareTo(RuleResponse o) {
        // 1. Higher Tier first
        int tierCompare = Integer.compare(o.tier.getLevel(), this.tier.getLevel());
        if (tierCompare != 0) return tierCompare;

        // 2. Higher Score first
        int scoreCompare = Integer.compare(o.score, this.score);
        if (scoreCompare != 0) return scoreCompare;

        // 3. Absolute Tie-breaker: ModuleID alphabetical order
        return this.module.name().compareTo(o.module.name());
    }
}
