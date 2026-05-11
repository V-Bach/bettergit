package gitv.git;

import java.io.File;

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
        boolean hasUnpushedCommits = gitService.hasUnpushedCommits(status);
        boolean hasRemote = gitService.hasRemote();
        boolean hasUnmergedPaths = gitService.hasUnmergedPaths(status);

        boolean isLocked = false;
        String lockReason = null;
        
        File gitDir = new File(gitService.getRepoRoot(), ".git");
        if (gitDir.exists() && gitDir.isDirectory()) {
            if (new File(gitDir, "index.lock").exists()) {
                isLocked = true;
                lockReason = "index.lock found (Another Git process is running)";
            } else if (new File(gitDir, "MERGE_HEAD").exists()) {
                isLocked = true;
                lockReason = "MERGE_HEAD found (Unresolved merge)";
            } else if (new File(gitDir, "REBASE_HEAD").exists() || new File(gitDir, "rebase-apply").exists() || new File(gitDir, "rebase-merge").exists()) {
                isLocked = true;
                lockReason = "rebase in progress";
            } else if (new File(gitDir, "CHERRY_PICK_HEAD").exists()) {
                isLocked = true;
                lockReason = "CHERRY_PICK_HEAD found (Paused cherry-pick)";
            }
        }

        return new RepoContext(hasUnstagedChanges, hasStagedChanges, hasUnpushedCommits, isAheadOfRemote, isBehindRemote, hasRemote, hasUnmergedPaths, isLocked, lockReason);
    }
}
