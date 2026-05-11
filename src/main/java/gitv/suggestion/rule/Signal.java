package gitv.suggestion.rule;

public enum Signal {
    STAGED_CHANGES,
    UNSTAGED_CHANGES,
    AHEAD_REMOTE,
    BEHIND_REMOTE,
    NO_REMOTE,
    DIVERGED,
    UNMERGED_PATHS
}
