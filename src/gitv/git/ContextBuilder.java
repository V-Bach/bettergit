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
        boolean hasChanges = changedFiles > 0;
        boolean hasUnpushedCommits = gitService.hasUnpushedCommits();

        return new RepoContext(hasChanges, hasUnpushedCommits);
    }
}
