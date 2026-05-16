# Configuration Precedence Order

When determining which setting to use, Gitv strictly follows a predefined precedence order. If a setting is defined in multiple places, the most specific configuration wins.

## Order of Evaluation (Highest to Lowest)

1. **Command-Line Flags**
   Flags passed directly to the command (e.g., `--apply`, `--explain`) always override any saved configuration or environment variables.

2. **Environment Variables**
   Variables prefixed with `GITV_` (e.g., `GITV_EXECUTION_MODE=INTERACTIVE`). Primarily used in CI/CD environments.

3. **Repository-Local Configuration**
   Settings defined in `.git/gitv-config` or `.gitv/config` within the current project.

4. **Global/User Configuration**
   Settings defined in the user's home directory (e.g., `~/.gitvconfig`).

5. **Gitv Default Settings**
   The built-in default values defined in Gitv's core architecture.

## Why This Matters

This strict ordering ensures that:
- You can define safe, global defaults.
- A specific project can enforce stricter rules (e.g., enforcing `GUARDED` execution mode).
- You can always override settings temporarily in a terminal using flags or environment variables without altering files.
