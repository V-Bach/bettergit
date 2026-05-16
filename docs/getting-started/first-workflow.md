# Your First Workflow with Gitv

Let's walk through a real-world scenario where Gitv shines: syncing your work while your colleagues have pushed changes to the remote.

## The Scenario

You are working on the `main` branch. You've modified `app.js` and `index.html`. Meanwhile, a colleague pushed a critical bug fix to the `main` remote.

In standard Git, your workflow looks like this:
1. `git stash`
2. `git pull --rebase`
3. `git stash pop`
4. Handle potential stash conflicts.

With Gitv, you simply focus on your goal.

## Step 1: Assess the Situation

Run `gitv status`:
```bash
gitv status
```
Gitv detects that your working tree is dirty *and* your branch is behind the remote.

## Step 2: Ask for a Plan

Ask Gitv how it intends to handle this:
```bash
gitv go --explain
```
Gitv responds:
> **Workflow Plan:**
> 1. Stash uncommitted changes (app.js, index.html)
> 2. Fetch and Rebase against origin/main
> 3. Pop stashed changes

## Step 3: Let Gitv Execute

Run the command to execute the plan:
```bash
gitv go
```
Gitv will seamlessly execute the steps. If no conflicts occur, you are successfully synced and ready to continue working.

## What if there's a conflict?

If Step 3 (stash pop) results in a conflict, Gitv's **SafetyValidator** will intervene. It will immediately halt execution, protect your repository state, and output clear instructions on how to resolve the conflict and continue.
