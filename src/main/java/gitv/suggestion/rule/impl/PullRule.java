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
                ModuleID.PULL_REBASE, null, Anchor.SYNC, 
                new gitv.workflow.Advisory("Your teammates have uploaded new code. You need to download it before making changes.", gitv.workflow.Severity.WARNING, gitv.workflow.SuggestionType.FIX, null),
                gitv.workflow.ExecutionMode.INTERACTIVE, true
            ));
        }
        return Optional.empty();
    }
}
