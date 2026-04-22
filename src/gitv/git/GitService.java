package gitv.git;

import java.io.*;

public class GitService {
    private File repoRoot = null;

    private synchronized File getRepoRoot() {
        if (repoRoot != null) {
            return repoRoot;
        }
        try {
            ProcessBuilder builder = new ProcessBuilder("git", "rev-parse", "--show-toplevel");
            builder.directory(new File(System.getProperty("user.dir")));
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String topLevel = reader.readLine();
            int exitCode = process.waitFor();

            if (exitCode == 0 && topLevel != null && !topLevel.trim().isEmpty()) {
                repoRoot = new File(topLevel.trim());
            } else {
                repoRoot = new File(System.getProperty("user.dir"));
            }
        } catch (Exception e) {
            repoRoot = new File(System.getProperty("user.dir"));
        }
        return repoRoot;
    }

    public String getCurrentBranch() {
        CommandResult result = runCommand("git", "branch", "--show-current");
        return result.isSuccess() ? result.output.trim() : "";
    }

    public String getStatus() {
        CommandResult result = runCommand("git", "status", "--porcelain=v1", "-b");
        return result.isSuccess() ? result.output : "";
    }

    public boolean isGitRepository() {
        CommandResult result = runCommand("git", "rev-parse", "--is-inside-work-tree");
        return result.isSuccess() && result.output.trim().equals("true");
    }

    private CommandResult runCommand(String... command) {
        StringBuilder output = new StringBuilder();
        int exitCode = -1;

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(getRepoRoot());
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            exitCode = process.waitFor();
        } catch (Exception e) {
            output.append(e.getMessage());
        }
        return new CommandResult(output.toString(), exitCode);
    }

    public int countChangedFiles(String status) {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }

        int count = 0;
        String[] lines = status.split("\\r?\\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public boolean commit(String message) {
        CommandResult result = runCommand("git", "commit", "-m", message);
        return result.isSuccess();
    }

    public boolean push() {
        CommandResult result = runCommand("git", "push");
        return result.isSuccess();
    }

    public boolean pull() {
        CommandResult result = runCommand("git", "pull");
        return result.isSuccess();
    }

    public boolean hasMergeConflicts() {
        CommandResult result = runCommand("git", "ls-files", "-u");
        return result.isSuccess() && !result.output.trim().isEmpty();
    }

    public boolean hasUnpushedCommits() {
        CommandResult result = runCommand("git", "status");
        return result.isSuccess() && result.output.contains("ahead of");
    }

    public boolean hasStagedChanges(String status) {
        if (status == null || status.trim().isEmpty()) return false;
        String[] lines = status.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("##")) continue;
            if (line.length() > 0) {
                char x = line.charAt(0);
                if (x == 'M' || x == 'A' || x == 'D' || x == 'R' || x == 'C') {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasUnstagedChanges(String status) {
        if (status == null || status.trim().isEmpty()) return false;
        String[] lines = status.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("##")) continue;
            if (line.length() > 1) {
                char y = line.charAt(1);
                if (y == 'M' || y == 'D' || line.startsWith("??")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAheadOfRemote(String status) {
        if (status == null || status.trim().isEmpty()) return false;
        String[] lines = status.split("\\r?\\n");
        if (lines.length > 0 && lines[0].startsWith("##")) {
            return lines[0].contains("ahead ");
        }
        return false;
    }

    public boolean isBehindRemote(String status) {
        if (status == null || status.trim().isEmpty()) return false;
        String[] lines = status.split("\\r?\\n");
        if (lines.length > 0 && lines[0].startsWith("##")) {
            return lines[0].contains("behind ");
        }
        return false;
    }
}
