# Contributing to Gitv

Thank you for your interest in contributing to Gitv! We welcome contributions from the community to help make Gitv the best workflow orchestrator available.

## Getting Started

1. **Fork the Repository:** Start by forking the Gitv repository to your GitHub account.
2. **Clone Locally:** Clone your fork to your local machine.
3. **Set Up the Environment:** Follow the [Local Development Setup](../setup/local-development.md) guide to configure your build environment (JDK, Maven/Gradle, GraalVM).

## Contribution Workflow

We follow a standard Pull Request workflow:

1. **Create a Branch:** Create a feature branch from `main` (e.g., `feature/add-new-rule`).
2. **Make Changes:** Implement your feature or bug fix.
3. **Write Tests:** Ensure your code is covered by unit tests, particularly if altering the `DecisionEngine` or `RepoContext`.
4. **Run Verification:** Run the full build and test suite locally.
5. **Submit a PR:** Open a Pull Request against the upstream `main` branch. Provide a clear description of the problem and your solution.

## Review Process

All submissions are reviewed by the core maintainers. Reviews will focus on:
- Adherence to the [Architecture Rules](architecture-rules.md).
- Code quality and [Coding Standards](coding-standards.md).
- Test coverage.
- Clear and helpful commit messages.

*Please note that architectural purity is heavily guarded in this project. PRs that violate the "One Brain" principle will require refactoring.*
