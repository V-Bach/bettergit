package gitv.suggestion.rule.impl;

import gitv.suggestion.rule.*;
import gitv.workflow.Anchor;
import gitv.workflow.ModuleID;

import java.util.Optional;
import java.util.Set;

public class CommitRule implements ActionRule {
    @Override
    public Optional<RuleResponse> evaluate(Set<Signal> signals) {
        if (signals.contains(Signal.STAGED_CHANGES) || signals.contains(Signal.UNSTAGED_CHANGES)) {
            Goal goal = signals.contains(Signal.NO_REMOTE) ? Goal.COMMIT_LOCAL : Goal.SYNCHRONIZE;
            return Optional.of(new RuleResponse(
                goal, Tier.WORKFLOW, 100, 
                ModuleID.COMMIT, null, Anchor.COMMIT, 
                "Changes to commit"
            ));
        }
        return Optional.empty();
    }
}
