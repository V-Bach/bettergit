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
        String branch = gitService.getCurrentBranch();
        String status = gitService.getStatus();
        int changedFiles = gitService.countChangedFiles(status);

        return new RepoContext(branch, changedFiles);
    }
}
