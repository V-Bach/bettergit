# The Safety Model

Gitv is designed to be a "guided co-pilot." It prioritizing repository safety above all else. This safety is enforced by the **SafetyValidator**.

## The Role of the SafetyValidator

The SafetyValidator is a dedicated architectural layer that acts as the final gatekeeper before the **Execution Engine** runs a plan.

**Crucial Rules:**
- The SafetyValidator is **not allowed to think**. It cannot add, modify, or reorder steps in the plan.
- It can only perform read-only checks on the plan against predefined safety bounds.
- If a plan violates a safety rule, the Validator throws a strict, user-facing error and halts execution.

## What Does it Protect Against?

The SafetyValidator prevents scenarios like:
- Executing a `HARD_RESET` if the working directory contains uncommitted, un-stashed changes.
- Attempting a `STASH_POP` if a previous rebase step failed.
- Force pushing to a protected branch like `main`.

## Safety in Intermediate States

If a Git operation (like a pull or rebase) stops midway due to a conflict, Gitv recognizes this intermediate state. The SafetyValidator will refuse to execute new workflows until the `RepoContext` indicates the conflict is resolved, preventing users from making a bad situation worse.
