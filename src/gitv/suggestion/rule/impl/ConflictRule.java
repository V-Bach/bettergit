package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;

import java.util.Optional;
import java.util.Set;

public class ConflictRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (signals.contains(Signal.UNMERGED_PATHS)) {
            return Optional.of(new RuleResponse(
                Goal.STABILIZE, Tier.EMERGENCY, 2000, 
                ModuleID.NONE, null, Anchor.SYNC, 
                new gitv.workflow.Advisory("There is a clash between your edits and someone else's. We need to manually decide which edits to keep before we can proceed.", gitv.workflow.Severity.DANGER, gitv.workflow.SuggestionType.BLOCKER, null),
                gitv.workflow.ExecutionMode.INTERACTIVE, false
            ));
        }
        return Optional.empty();
    }
}
