# Crash Recovery & Idempotency

Gitv is built to handle the chaotic reality of local development environments: processes get killed, laptops run out of battery, and Git commands fail unpredictably.

## The Challenge

If a workflow plan involves three steps (`STASH`, `PULL`, `POP`) and the user `Ctrl+C`s during the `PULL`, the repository is left in an intermediate state. Running `gitv go` again shouldn't start from scratch and attempt to stash again.

## State Persistence

Gitv maintains a lightweight persistence layer (typically a JSON-based state file in `.gitv/state`) that tracks:
- The current executing plan.
- The status of each step (`PENDING`, `COMPLETED`, `FAILED`).
- The `HEAD` hash at the start of execution.

## Idempotent Execution

When the **Execution Engine** starts, it checks the state file. If an interrupted plan exists, it attempts to resume:
1. It validates the current `RepoContext` against the expected state.
2. It filters out already `COMPLETED` steps.
3. It passes the remaining steps through the **SafetyValidator**.
4. If safe, it resumes execution from the exact point of failure.

This ensures that workflows are idempotent; running `gitv go` repeatedly will safely advance the state without duplicating non-repeatable actions.
