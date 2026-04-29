package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;

import java.util.Optional;
import java.util.Set;

public class DivergenceRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (signals.contains(Signal.DIVERGED)) {
            return Optional.of(new RuleResponse(
                Goal.STABILIZE, Tier.EMERGENCY, 1000, 
                ModuleID.NONE, null, Anchor.SYNC, 
                new gitv.workflow.Advisory("Diverged branch requires manual resolution", gitv.workflow.Severity.DANGER, gitv.workflow.SuggestionType.BLOCKER, null),
                gitv.workflow.ExecutionMode.INTERACTIVE, false
            ));
        }
        return Optional.empty();
    }
}
