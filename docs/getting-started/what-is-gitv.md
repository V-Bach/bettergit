# What is Gitv?

Gitv is a **context-aware, safe, and automated workflow orchestrator** for Git. It is designed to act as a co-pilot for your version control, removing the cognitive overhead of standard Git commands while maintaining strict safety guarantees.

Unlike simple shell aliases or basic wrappers, Gitv understands the full state of your repository. It analyzes your working directory, staging area, branch status, and remote state to deterministically plan the exact sequence of actions needed to achieve your goal.

## The Problem with Standard Git

Standard Git is incredibly powerful, but it requires you to manually manage state transitions. You must remember to stash, pull, rebase, pop, resolve conflicts, and commit in the exact right order. A single mistake can lead to detached HEADs, lost work, or complex merge conflicts.

## The Gitv Solution

Gitv replaces this manual state management with a **decision engine**. When you ask Gitv to sync your work, it:
1. **Reads** the repository context (uncommitted changes, behind/ahead counts, etc.).
2. **Plans** a safe sequence of actions.
3. **Explains** exactly what it intends to do.
4. **Executes** the plan automatically, with guardrails in place to stop if something goes wrong.

## Key Features

- **Workflow-Oriented:** Stop thinking in terms of `fetch`, `merge`, and `rebase`. Start thinking in terms of `sync`, `save`, and `publish`.
- **Explainable:** Gitv will never run a complex sequence of commands without the ability to explain *why* it's running them. Try `gitv go --explain`.
- **Safe by Default:** Gitv employs a robust safety model. It will refuse to perform destructive actions automatically and will guide you safely through error states.
