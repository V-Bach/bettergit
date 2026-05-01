package gitv.git;

public class RepoContext {
    private final boolean hasUnstagedChanges;
    private final boolean hasStagedChanges;
    private final boolean hasUnpushedCommits;
    private final boolean isAheadOfRemote;
    private final boolean isBehindRemote;
    private final boolean hasRemote;
    private final boolean hasUnmergedPaths;
    private final boolean isLocked;
    private final String lockReason;

    public RepoContext(boolean hasUnstagedChanges, boolean hasStagedChanges, boolean hasUnpushedCommits, boolean isAheadOfRemote, boolean isBehindRemote, boolean hasRemote, boolean hasUnmergedPaths, boolean isLocked, String lockReason) {
        this.hasUnstagedChanges = hasUnstagedChanges;
        this.hasStagedChanges = hasStagedChanges;
        this.hasUnpushedCommits = hasUnpushedCommits;
        this.isAheadOfRemote = isAheadOfRemote;
        this.isBehindRemote = isBehindRemote;
        this.hasRemote = hasRemote;
        this.hasUnmergedPaths = hasUnmergedPaths;
        this.isLocked = isLocked;
        this.lockReason = lockReason;
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
    
    public boolean hasRemote() {
        return hasRemote;
    }

    public boolean hasUnmergedPaths() {
        return hasUnmergedPaths;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getLockReason() {
        return lockReason;
    }

    public RepoContext withStagedChanges(boolean hasStagedChanges) {
        return new RepoContext(this.hasUnstagedChanges, hasStagedChanges, this.hasUnpushedCommits, this.isAheadOfRemote, this.isBehindRemote, this.hasRemote, this.hasUnmergedPaths, this.isLocked, this.lockReason);
    }

    public RepoContext withAheadOfRemote(boolean isAheadOfRemote) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, this.hasUnpushedCommits, isAheadOfRemote, this.isBehindRemote, this.hasRemote, this.hasUnmergedPaths, this.isLocked, this.lockReason);
    }

    public RepoContext withBehindRemote(boolean isBehindRemote) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, this.hasUnpushedCommits, this.isAheadOfRemote, isBehindRemote, this.hasRemote, this.hasUnmergedPaths, this.isLocked, this.lockReason);
    }

    public RepoContext withUnpushedCommits(boolean hasUnpushedCommits) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, hasUnpushedCommits, this.isAheadOfRemote, this.isBehindRemote, this.hasRemote, this.hasUnmergedPaths, this.isLocked, this.lockReason);
    }
    
    public RepoContext withRemote(boolean hasRemote) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, this.hasUnpushedCommits, this.isAheadOfRemote, this.isBehindRemote, hasRemote, this.hasUnmergedPaths, this.isLocked, this.lockReason);
    }

    public RepoContext withUnmergedPaths(boolean hasUnmergedPaths) {
        return new RepoContext(this.hasUnstagedChanges, this.hasStagedChanges, this.hasUnpushedCommits, this.isAheadOfRemote, this.isBehindRemote, this.hasRemote, hasUnmergedPaths, this.isLocked, this.lockReason);
    }
}
