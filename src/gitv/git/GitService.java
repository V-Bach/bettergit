package gitv.git;

import java.io.*;

public class GitService {
    public String getCurrentBranch() {
        CommandResult result = runCommand("git branch --show-current");
        return result.isSuccess() ? result.output.trim() : "";
    }

    public String getStatus() {
        CommandResult result = runCommand("git status --porcelain");
        return result.isSuccess() ? result.output : "";
    }

    public boolean isGitRepository() {
        CommandResult result = runCommand("git rev-parse --is-inside-work-tree");
        return result.isSuccess() && result.output.trim().equals("true");
    }

    private CommandResult runCommand(String command) {
        StringBuilder output = new StringBuilder();
        int exitCode = -1;

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.directory(new File(System.getProperty("user.dir")));
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

    public boolean commitAll() {
        CommandResult result = runCommand("git add . && git commit -m \"auto commit\"");
        return result.isSuccess();
    }

    public boolean push() {
        CommandResult result = runCommand("git push");
        return result.isSuccess();
    }

    public boolean pull() {
        CommandResult result = runCommand("git pull");
        return result.isSuccess();
    }

    public boolean hasMergeConflicts() {
        CommandResult result = runCommand("git ls-files -u");
        return result.isSuccess() && !result.output.trim().isEmpty();
    }

    public boolean hasUnpushedCommits() {
        CommandResult result = runCommand("git status");
        return result.isSuccess() && result.output.contains("ahead of");
    }
}
