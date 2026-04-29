package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;

import java.util.Optional;
import java.util.Set;

public class PushRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (!signals.contains(Signal.NO_REMOTE) && !signals.contains(Signal.BEHIND_REMOTE)) {
            if (signals.contains(Signal.AHEAD_REMOTE) || signals.contains(Signal.STAGED_CHANGES) || signals.contains(Signal.UNSTAGED_CHANGES)) {
                return Optional.of(new RuleResponse(
                    Goal.SYNCHRONIZE, Tier.WORKFLOW, 80, 
                    ModuleID.PUSH, null, Anchor.POST_COMMIT, 
                    new gitv.workflow.Advisory("Changes to push", gitv.workflow.Severity.INFO, gitv.workflow.SuggestionType.HINT, null),
                    gitv.workflow.ExecutionMode.GUARDED, true
                ));
            }
        }
        return Optional.empty();
    }
}
