# Installation Problems

If you are having trouble installing or running Gitv, check these common issues.

## Gitv is not recognized as a command

**Symptoms:**
You receive an error like `gitv: command not found` or `'gitv' is not recognized as an internal or external command`.

**Solution:**
The directory containing the Gitv executable is not in your system's `PATH`.
- **Homebrew/Scoop:** These managers usually handle the PATH automatically. Try restarting your terminal session.
- **Manual/Script Install:** Ensure you moved the binary to a directory like `/usr/local/bin` (macOS/Linux) and that this directory is included in your `PATH` environment variable.

## Cannot execute binary file (macOS/Linux)

**Symptoms:**
You receive `cannot execute binary file: Exec format error`.

**Solution:**
You downloaded a binary compiled for a different system architecture.
- Check if you are on an Intel (`x86_64`) or Apple Silicon/ARM (`arm64`/`aarch64`) machine.
- Download the correct binary from the GitHub Releases page, or use the `install.sh` script which auto-detects your architecture.

## Anti-Virus Warnings (Windows)

**Symptoms:**
Windows Defender or other AV software blocks the execution of Gitv.

**Solution:**
Because Gitv modifies files (via Git), some overly aggressive heuristics might flag it. We provide checksums on our GitHub Releases. Verify the binary matches the checksum. If it matches, you may need to add an exception for the executable in your AV software.
