# Quick Start

Get up and running with Gitv in minutes. This guide walks you through a standard daily workflow.

## 1. Check Your Repository State

Navigate into any existing Git repository and use the `status` command. This replaces `git status` with a cleaner, workflow-oriented summary:

```bash
cd my-project
gitv status
```
*You'll see a clean output showing uncommitted changes, branch status, and whether you are in sync with your remote.*

## 2. Explain the Plan

Let's see what Gitv *would* do if you asked it to automatically advance your workflow. The `--explain` flag is your best friend:

```bash
gitv go --explain
```
*Gitv will output a human-readable plan. For example, if you have uncommitted changes and are behind the remote, it will plan to stash your changes, pull the remote updates, and pop your stash.*

## 3. Execute the Workflow

If you agree with the plan, drop the explain flag and let Gitv do the work:

```bash
gitv go
```
*Gitv will execute the planned steps safely. If a conflict occurs, it will pause and guide you through recovery.*

## 4. Check System Health

If things ever feel weird, or if Git is stuck in an intermediate state (like a paused rebase), run the doctor command:

```bash
gitv doctor
```
*Gitv will analyze the repository state, detect any issues (like a detached HEAD), and suggest the commands needed to fix them.*
