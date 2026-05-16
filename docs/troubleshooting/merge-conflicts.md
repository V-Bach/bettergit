# Handling Merge Conflicts

Merge conflicts are an inevitable part of collaborative development. Gitv is designed to help you resolve them safely rather than leaving you stranded in a broken state.

## How Gitv Reacts to Conflicts

When a conflict occurs (e.g., during a `gitv sync` operation performing a rebase), Gitv's **Execution Engine** immediately halts.

It will:
1. Stop executing the current plan.
2. Protect your repository from further automated changes.
3. Output a clear, beginner-friendly message explaining exactly which files are conflicting.

## Resolving the Conflict

When Gitv halts due to a conflict, follow these steps:

1. **Open your Editor:** Open the conflicting files in your code editor or IDE. Look for standard Git conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`).
2. **Resolve the Code:** Manually edit the files to keep the correct code and remove the markers.
3. **Save the Files.**
4. **Mark as Resolved:** Run standard Git commands to stage the resolved files:
   ```bash
   git add <resolved-file>
   ```

## Resuming the Workflow

Once all conflicts are resolved and staged, you can simply run `gitv go` again. 

Because of Gitv's **Crash Recovery** and idempotency architecture, it will detect that the conflict is resolved, recognize the interrupted rebase/merge, and safely resume the remainder of the original workflow.
