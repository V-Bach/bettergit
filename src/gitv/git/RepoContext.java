package gitv.git;

public class RepoContext {
    private final boolean hasChanges;
    private final boolean hasUnpushedCommits;

    public RepoContext(boolean hasChanges, boolean hasUnpushedCommits) {
        this.hasChanges = hasChanges;
        this.hasUnpushedCommits = hasUnpushedCommits;
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public boolean hasUnpushedCommits() {
        return hasUnpushedCommits;
    }
}
