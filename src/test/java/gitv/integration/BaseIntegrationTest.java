package gitv.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Base class for Integration Tests.
 * This class automatically provisions a temporary local Git repository
 * for each test and tears it down afterward.
 */
public abstract class BaseIntegrationTest {

    protected Path tempRepoDir;

    @BeforeEach
    public void setUpRepo() throws IOException, InterruptedException {
        // Create temporary directory for the repository
        tempRepoDir = Files.createTempDirectory("gitv-test-repo-");
        
        // Initialize an empty Git repository
        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(tempRepoDir.toFile());
        Process p = pb.start();
        p.waitFor();

        // Optional: Perform additional setup (e.g., configuring dummy user)
        runGitCommand("config", "user.name", "Gitv TestUser");
        runGitCommand("config", "user.email", "test@gitv.local");
    }

    @AfterEach
    public void tearDownRepo() throws IOException {
        if (tempRepoDir != null && Files.exists(tempRepoDir)) {
            // Delete the temporary directory and all its contents
            Files.walk(tempRepoDir)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    /**
     * Helper method to execute arbitrary Git commands in the temporary repository.
     */
    protected String runGitCommand(String... args) throws IOException, InterruptedException {
        String[] command = new String[args.length + 1];
        command[0] = "git";
        System.arraycopy(args, 0, command, 1, args.length);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(tempRepoDir.toFile());
        Process p = pb.start();
        p.waitFor();

        return new String(p.getInputStream().readAllBytes()).trim();
    }
}
