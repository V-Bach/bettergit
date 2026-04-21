package gitv.git;

public class RepoContext {
    private final boolean hasUnstagedChanges;
    private final boolean hasStagedChanges;
    private final boolean hasUnpushedCommits;
    private final boolean isAheadOfRemote;
    private final boolean isBehindRemote;

    public RepoContext(boolean hasUnstagedChanges, boolean hasStagedChanges, boolean hasUnpushedCommits, boolean isAheadOfRemote, boolean isBehindRemote) {
        this.hasUnstagedChanges = hasUnstagedChanges;
        this.hasStagedChanges = hasStagedChanges;
        this.hasUnpushedCommits = hasUnpushedCommits;
        this.isAheadOfRemote = isAheadOfRemote;
        this.isBehindRemote = isBehindRemote;
    }

    public boolean hasUnstagedChanges() {
        return hasUnstagedChanges;
    }

    public boolean hasStagedChanges() {
        return hasStagedChanges;
    }

    public boolean hasUnpushedCommits() {
        return hasUnpushedCommits;
    }

    public boolean isAheadOfRemote() {
        return isAheadOfRemote;
    }

    public boolean isBehindRemote() {
        return isBehindRemote;
    }

    public RepoContext withStagedChanges(boolean hasStagedChanges) {
        return new RepoContext(this.hasUnstagedChanges, hasStagedChanges, this.hasUnpushedCommits, this.isAheadOfRemote, this.isBehindRemote);
    }

    public RepoContext withAheadOfRemote(boolean isAheadOfRemote) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, this.hasUnpushedCommits, isAheadOfRemote, this.isBehindRemote);
    }

    public RepoContext withBehindRemote(boolean isBehindRemote) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, this.hasUnpushedCommits, this.isAheadOfRemote, isBehindRemote);
    }

    public RepoContext withUnpushedCommits(boolean hasUnpushedCommits) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, hasUnpushedCommits, this.isAheadOfRemote, this.isBehindRemote);
    }
}
