# Gitv Productization & Distribution Roadmap

This document serves as the formal engineering plan for transitioning Gitv from a local Java project to a standalone, zero-dependency, globally installable CLI product. 

As requested, this roadmap completely avoids Docker, respects existing boundaries, and focuses strictly on native CLI productization tradeoffs.

---

## 1. Project Restructuring (Maven Migration)

Gitv is currently a raw Java application. We must migrate it to a standard Maven structure to utilize dependency management, plugin execution, and the GraalVM toolchain.

### Folder Strategy
```text
gitv/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── gitv/
│   │   │       ├── cli/         # NEW: Picocli command definitions & IO logic
│   │   │       ├── engine/      # EXISTING: ExecutionEngine, StateManager
│   │   │       └── util/        # EXISTING: Utilities
│   │   └── resources/
│   │       ├── application.properties
│   │       └── META-INF/
│   │           └── native-image/ # NEW: GraalVM reflection hints
└── .github/
    └── workflows/               # NEW: CI/CD automation
```

### Decoupling CLI vs. Engine
- The `gitv.engine` layer must remain oblivious to the CLI context. It should accept domain objects (e.g., `SyncRequest`, `RepoContext`) and return outcome objects (`ExecutionResult`).
- The `gitv.cli` package is responsible for terminal rendering, ANSI color coding, error formatting, and input parsing.

---

## 2. Picocli Integration

### Command Hierarchy
We will use Picocli to route commands cleanly without messy nested `if/else` checks.

```java
@Command(name = "gitv", mixinStandardHelpOptions = true, version = "1.0.0",
         description = "Autonomous Git workflow engine.",
         subcommands = { SyncCmd.class, DoctorCmd.class, CommitCmd.class })
public class GitvRootCmd implements Callable<Integer> {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new GitvRootCmd()).execute(args);
        System.exit(exitCode);
    }
    
    @Override
    public Integer call() {
        // Fallback if user just runs `gitv` without subcommands
        System.out.println("Use `gitv --help` for available commands.");
        return 0; 
    }
}
```

### Subcommand Structure
Each command (e.g., `SyncCmd`) implements `Callable<Integer>`. The `call()` method will:
1. Parse flags/options.
2. Instantiate the core `ExecutionEngine`.
3. Handle exceptions cleanly (returning non-zero exit codes).
4. Output professional user-facing text.

---

## 3. Fat JAR Packaging

Before compiling natively, the intermediary step is building a standalone Fat JAR that bundles all dependencies.

- **Maven Plugin**: Use `maven-shade-plugin` over `maven-assembly-plugin`. It manages the merging of `META-INF/services` files cleanly, which is critical if transitive dependencies use Java SPIs.
- **Main Entrypoint**: The Shade plugin's `ManifestResourceTransformer` will point strictly to `gitv.cli.GitvRootCmd`.

---

## 4. GraalVM Native Image Migration

This is the most critical and fragile step. The goal is to produce an Ahead-Of-Time (AOT) compiled binary that requires no JVM installed on the user's machine.

### Exact Challenges
1. **Reflection**: Java reflection is evaluated at runtime. GraalVM compiles AOT. If you are using any libraries (like Jackson/Gson for Phase 4.5 state persistence), you **must** supply reflection hints via `reflect-config.json` in `META-INF/native-image/`.
2. **Picocli Overhead**: Use the `picocli-codegen` Maven annotation processor. This processes your CLI annotations at compile-time, eliminating the need for runtime reflection during argument parsing.
3. **Cross-Compilation**: GraalVM **cannot** cross-compile. A macOS runner builds the macOS binary; a Windows runner builds the `.exe`. 
4. **Logging Caveats**: Heavy logging frameworks (Logback, Log4j) severely bloat native image size and slow down startup. Stick strictly to `java.util.logging` or plain `System.out` with ANSI codes.

---

## 5. GitHub Actions CI/CD Pipeline

To ensure automated, repeatable builds, the CI pipeline needs specific workflow stages.

### Workflow Stages
1. **Test Matrix**: Run JUnit tests across `ubuntu-latest`, `windows-latest`, `macos-latest`.
2. **GraalVM Setup**: Utilize `graalvm/setup-graalvm` to inject the correct GraalVM JDK into the runner.
3. **Matrix Native Build**:
   - `mvn -Pnative package` will be run concurrently on Linux (ELF), Windows (.exe), and Mac (Mach-O).
   - **Note**: Use both `macos-latest` (ARM64 Apple Silicon) and `macos-13` (x86_64 Intel) to support all Mac users.
4. **Release Automation**: Triggered automatically on tag push (`git tag v1.0.0`). The workflow bundles binaries and creates a GitHub Release using `softprops/action-gh-release`.

---

## 6. GitHub Release Strategy

### Asset Structure & Naming Conventions
The release artifacts should be immediately understandable by end-users and package managers:
- `gitv-linux-amd64.tar.gz`
- `gitv-darwin-amd64.tar.gz` (Intel Mac)
- `gitv-darwin-arm64.tar.gz` (Apple Silicon Mac)
- `gitv-windows-amd64.zip`

### Archiving Strategy
Do not upload naked executables. Wrap them in a `.tar.gz` or `.zip` accompanied by a `LICENSE` file and a minimal `README.md`.

---

## 7. install.sh Architecture

For non-package-manager users, a standard `curl | bash` script is necessary.

- **Detection logic**: Script utilizes `uname -s` and `uname -m` to determine the correct binary string (`darwin-arm64`, `linux-amd64`, etc.).
- **Download logic**: Curls the specific release archive from GitHub.
- **Path Management**: Extracts the binary into `~/.local/bin/gitv` (or `/usr/local/bin` if `sudo` is granted). Checks if `~/.local/bin` is in `$PATH` and warns the user if not.
- **Upgrades**: Running the script again simply overwrites the binary.

---

## 8. Homebrew Integration (macOS/Linux)

- **Tap Structure**: Create a secondary GitHub repository: `your-username/homebrew-gitv`.
- **Formula Generation**: Create a `gitv.rb` Ruby formula.
- **Crucial Tradeoff**: **Do not compile from source in the formula.** GraalVM requires massive RAM and time. The formula should simply `url "https://github.com/..."` pointing to the pre-compiled Darwin binaries and run `bin.install "gitv"`.
- **Release Automation**: Add a step to your CI/CD Release pipeline to use `peter-evans/repository-dispatch` or directly commit to the Tap repo, bumping the formula version and SHA-256 hash.

---

## 9. Scoop Integration (Windows)

Scoop handles local, isolated installations on Windows elegantly.

- **Manifest Structure**: Create a `gitv.json` hosted in a bucket repository or directly on your main repo.
- **Manifest Definition**:
  ```json
  {
    "version": "1.0.0",
    "architecture": {
      "64bit": {
        "url": "https://github.com/your-username/gitv/releases/download/v1.0.0/gitv-windows-amd64.zip",
        "bin": "gitv.exe"
      }
    },
    "checkver": "github",
    "autoupdate": {
      "architecture": {
        "64bit": {
          "url": "https://github.com/your-username/gitv/releases/download/v$version/gitv-windows-amd64.zip"
        }
      }
    }
  }
  ```

---

## 10. Production-Readiness Checklist

Before creating `v1.0.0`, the CLI needs UX polish to obscure the underlying Java ecosystem.

- [ ] **Crash Handling**: Implement a global `Thread.setDefaultUncaughtExceptionHandler()`. Intercept raw Java stack traces. Print `[FATAL] Gitv encountered an error: <message>`. Append `Run with --verbose for trace` instead of vomiting Java exceptions into the user's terminal.
- [ ] **Config Directory**: Standardize local state resolution. E.g., Use `~/.config/gitv/` or `.gitv/` at the repository root.
- [ ] **Help & Version**: Guarantee `gitv --help` is colorized, clean, and reads like a native tool, not a Java app.
- [ ] **Telemetry (Warning)**: Avoid adding telemetry for V1. CLI developers are highly sensitive to unannounced tracking. Keep it purely local.

---

## 11. Brutally Honest Risk Analysis

* **GraalVM CI Constraints**: GraalVM Native Image compilation takes 5-10 minutes and heavily consumes RAM (up to 8GB). GitHub Actions free-tier runners (7GB RAM) frequently suffer Out-Of-Memory (OOM) kills during this phase. You will need to carefully tune JVM flags for the builder (e.g., `native-image -J-Xmx5G`).
* **Windows Antivirus Flagging**: Binaries built via GraalVM `.exe` often trigger false positives in Windows Defender because they lack Authenticode signatures (which cost money and validation time). Users may need to bypass warnings.
* **Maintenance Burden**: Every supported package manager (Homebrew, Scoop, install.sh) introduces an automation surface area that can break. If a release action fails midway, you might have out-of-sync binaries across platforms.
* **Cross-platform Git Assumptions**: Your engine must gracefully handle Windows paths `\` vs Unix paths `/`, differing text encoding, and differences between calling `git.exe` on Windows vs `git` on bash.

---

## 12. Recommended Execution Order (Realistic Roadmap)

Do NOT attempt to jump straight to GitHub Actions or Homebrew. Take this staged approach:

1. **Phase 1: Foundation (Days 1-2)**
   - Create `pom.xml`, restructure directories to Maven format.
   - Introduce Picocli. Map out the `gitv sync`, `doctor`, and `commit` commands.
   - Refactor `Main.java` to instantiate the command hierarchy.

2. **Phase 2: Local Fat JAR (Days 3-4)**
   - Configure `maven-shade-plugin`.
   - Build `gitv-1.0.jar`.
   - Run manual QA locally using `java -jar gitv-1.0.jar sync`. Verify engine separation is intact. Implement global exception handling to hide Java traces.

3. **Phase 3: GraalVM Prototyping (Days 5-7)**
   - Install GraalVM locally.
   - Run the native-image compilation.
   - Address reflection errors (the hardest part). Add `picocli-codegen`.
   - Generate local binary and test cold-start execution speed.

4. **Phase 4: CI/CD Pipeline (Days 8-10)**
   - Write the GitHub Actions matrix for Linux, Mac, Windows.
   - Optimize the build times.
   - Tag `v0.9.0-rc1` and verify the GH release triggers and binary artifacts are uploaded correctly.

5. **Phase 5: Distribution Ecosystem (Days 11-14)**
   - Write `install.sh`.
   - Create Homebrew Tap repo and `gitv.rb`.
   - Create Scoop manifest.
   - Test end-to-end installation UX.
