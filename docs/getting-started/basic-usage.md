# Basic Usage

Gitv simplifies standard Git operations into high-level workflow commands. Here are the core commands you'll use daily.

## `gitv status`

Provides a high-level, human-readable summary of your repository context.

```bash
gitv status
```
**Example Output:**
- **Branch:** `main` (in sync with `origin/main`)
- **Working Tree:** 2 files modified
- **Action Required:** You have uncommitted work.

## `gitv go`

The intelligent workflow advancer. `gitv go` analyzes your repository state and performs the most logical next steps to sync your work or push your branch.

```bash
# Preview the actions Gitv will take without running them
gitv go --explain

# Execute the planned workflow
gitv go
```

## `gitv sync`

Explicitly synchronize your local branch with the remote tracking branch. This safely handles stashing uncommitted changes, pulling, and popping the stash.

```bash
gitv sync

# To automatically apply the sync plan without asking for confirmation:
gitv sync --apply
```

## `gitv doctor`

Diagnoses and helps resolve sticky repository states, such as unresolved merge conflicts, incomplete cherry-picks, or detached HEADs.

```bash
gitv doctor
```
