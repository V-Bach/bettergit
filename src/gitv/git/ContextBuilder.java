package gitv.git;

public class ContextBuilder {
    private final GitService gitService;

    public ContextBuilder() {
        this.gitService = new GitService();
    }

    public boolean isGitRepository() {
        return gitService.isGitRepository();
    }

    public RepoContext build() {
        String status = gitService.getStatus();
        boolean hasStagedChanges = gitService.hasStagedChanges(status);
        boolean hasUnstagedChanges = gitService.hasUnstagedChanges(status);
        boolean isAheadOfRemote = gitService.isAheadOfRemote(status);
        boolean isBehindRemote = gitService.isBehindRemote(status);
        boolean hasUnpushedCommits = isAheadOfRemote; // unify with isAheadOfRemote logic
        boolean hasRemote = gitService.hasRemote();
        boolean hasUnmergedPaths = gitService.hasUnmergedPaths(status);

        return new RepoContext(hasUnstagedChanges, hasStagedChanges, hasUnpushedCommits, isAheadOfRemote, isBehindRemote, hasRemote, hasUnmergedPaths);
    }
}
