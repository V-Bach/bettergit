package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;

import java.util.Optional;
import java.util.Set;

public class PullRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (signals.contains(Signal.BEHIND_REMOTE) && !signals.contains(Signal.NO_REMOTE)) {
            return Optional.of(new RuleResponse(
                Goal.SYNCHRONIZE, Tier.WORKFLOW, 150, 
                ModuleID.PULL, null, Anchor.SYNC, 
                "Behind remote, pull required"
            ));
        }
        return Optional.empty();
    }
}
