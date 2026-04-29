package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;

import java.util.Optional;
import java.util.Set;

public class StageRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (signals.contains(Signal.UNSTAGED_CHANGES)) {
            // Only suggest Stage if there are no staged changes (to protect staging intent)
            if (!signals.contains(Signal.STAGED_CHANGES)) {
                Goal goal = signals.contains(Signal.NO_REMOTE) ? Goal.COMMIT_LOCAL : Goal.SYNCHRONIZE;
                return Optional.of(new RuleResponse(
                    goal, Tier.WORKFLOW, 90, 
                    ModuleID.STAGE, null, Anchor.PRE_COMMIT, 
                    new gitv.workflow.Advisory("Unstaged changes ready to stage", gitv.workflow.Severity.INFO, gitv.workflow.SuggestionType.HINT, null),
                    gitv.workflow.ExecutionMode.GUARDED, true
                ));
            }
        }
        return Optional.empty();
    }
}
