# Testing Guidelines

Gitv relies on a comprehensive test suite to guarantee its safety and deterministic behavior. All contributions must be accompanied by appropriate tests.

## Unit Testing

Unit tests form the bulk of our test suite. They are fast, isolated, and do not invoke actual Git processes.

- **Decision Engine Testing:** The most crucial tests. You must mock various `RepoContext` states (e.g., dirty working tree, detached head, ahead of remote) and assert that the `PlanBuilder` produces the exact expected sequence of steps.
- **SafetyValidator Testing:** Pass intentionally dangerous plans (e.g., `HARD_RESET` with uncommitted changes) to the Validator and assert that it throws the correct exception.
- **SignalLayer Testing:** Provide raw Git porcelain output strings to the parser and assert it generates the correct internal signals.

## Integration Testing

Integration tests verify that Gitv interacts correctly with the actual Git binary.

- **Setup:** Integration tests must create a temporary, isolated Git repository in a temporary directory for every test run.
- **Execution:** Run Gitv commands against the temporary repository and assert the resulting filesystem state.
- **Cleanup:** Ensure the temporary repositories are deleted after the test, regardless of success or failure.

## Test Driven Development

When adding features or fixing bugs, we encourage writing the failing test first. If a user reports a bug where Gitv misbehaves in a specific state, write a test that creates that `RepoContext` to reproduce the failure before implementing the fix.
