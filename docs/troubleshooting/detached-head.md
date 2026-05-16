# Escaping a Detached HEAD

A "detached HEAD" state in Git sounds scary, but it simply means you have checked out a specific commit directly rather than a branch name. While useful for inspecting history, it's dangerous for writing new code.

## How Gitv Helps

If you run `gitv status` or `gitv go` while in a detached HEAD state, Gitv detects this immediately via the **SignalLayer**.

Gitv will **refuse** to perform sync operations or advanced workflows while in this state, as any new commits you create could be easily lost.

## Fixing the State

To recover, Gitv will advise you to either discard your detached state or save it to a new branch.

**Option 1: Discard the inspection state and return to main**
```bash
git checkout main
```

**Option 2: Save any work you did in the detached state**
If you made commits while detached and want to keep them:
```bash
git checkout -b my-new-recovery-branch
```

Once you are back on a named branch, Gitv will resume normal operation. If you are unsure what to do, try running:
```bash
gitv doctor
```
Gitv will analyze the detached state and provide explicit commands to help you recover.
