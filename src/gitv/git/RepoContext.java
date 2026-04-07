package gitv.git;

public class RepoContext {
    private final String branch;
    private final int changedFiles;

    public RepoContext(String branch, int changedFiles) {
        this.branch = branch;
        this.changedFiles = changedFiles;
    }

    public String getBranch() {
        return branch;
    }

    public int getChangedFiles() {
        return changedFiles;
    }
}
