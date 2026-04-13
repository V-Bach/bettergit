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
        int changedFiles = gitService.countChangedFiles(status);
        boolean hasUnstagedChanges = changedFiles > 0; // naive mapping for now
        boolean hasStagedChanges = false;              // naive mapping for now
        boolean hasUnpushedCommits = gitService.hasUnpushedCommits();
        boolean isAheadOfRemote = false;               // naive mapping for now
        boolean isBehindRemote = false;                // naive mapping for now

        return new RepoContext(hasUnstagedChanges, hasStagedChanges, hasUnpushedCommits, isAheadOfRemote, isBehindRemote);
    }
}
