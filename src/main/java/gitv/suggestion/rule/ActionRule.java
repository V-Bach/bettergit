package gitv.suggestion.rule;

import java.util.Optional;
import java.util.Set;

public interface ActionRule {
    Optional<RuleResponse> evaluate(Set<Signal> signals);
}
