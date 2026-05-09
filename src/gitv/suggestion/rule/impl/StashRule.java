package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;
import gitv.engine.ActionKey;

import java.util.Optional;
import java.util.Set;

public class StashRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (signals.contains(Signal.UNSTAGED_CHANGES) && (signals.contains(Signal.BEHIND_REMOTE) || signals.contains(Signal.DIVERGED))) {
            return Optional.of(new RuleResponse(
                Goal.STABILIZE, Tier.WORKFLOW, 200, 
                ModuleID.STASH, null, Anchor.STASH_SAVE, 
                new gitv.workflow.Advisory("You have unsaved edits that conflict with downloading new code. Gitv will temporarily set them aside (stash) while we sync.", gitv.workflow.Severity.WARNING, gitv.workflow.SuggestionType.FIX, ActionKey.STASH),
                gitv.workflow.ExecutionMode.GUARDED, true
            ));
        }
        return Optional.empty();
    }
}
