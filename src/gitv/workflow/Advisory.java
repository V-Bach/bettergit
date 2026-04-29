package gitv.workflow;

import gitv.engine.ActionKey;

public record Advisory(
    String message,
    Severity severity,
    SuggestionType type,
    ActionKey actionableFix
) {}
