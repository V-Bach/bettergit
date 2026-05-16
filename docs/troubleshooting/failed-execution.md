# Dealing with Failed Executions

Gitv is designed to fail gracefully. If a command crashes or exits unexpectedly, your repository should remain safe.

## Identifying the Failure

If an automated workflow fails, Gitv will output an error message detailing the failure point.

Common causes include:
- Loss of network connectivity during a fetch/push.
- Insufficient permissions on a remote repository.
- A sudden process interruption (e.g., closing the terminal mid-execution).

## Step 1: Run Gitv Status

The first thing to do is check the state of your repository:
```bash
gitv status
```
This will tell you if you are in the middle of an operation (like a paused rebase) or if the repository is clean.

## Step 2: Use Gitv Doctor

If the state is messy or confusing, use the diagnostic tool:
```bash
gitv doctor
```
The doctor command analyzes the `RepoContext` and suggests safe commands to recover from intermediate states.

## Step 3: Resuming or Aborting

- **Resuming:** If the failure was transient (e.g., your wifi dropped), simply fix the network and run `gitv go` again. Gitv's **Crash Recovery** will attempt to resume the plan safely.
- **Aborting:** If a git operation is stuck (like an incomplete rebase) and you just want to return to how things were, standard Git abort commands still work perfectly:
  ```bash
  git rebase --abort
  ```
  Gitv respects all manual Git interventions.
