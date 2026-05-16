# Workflow Engine Architecture

At its core, Gitv is not merely a wrapper around Git commands; it is a **deterministic workflow engine**. The architecture is designed around the "One Brain" principle: all decisions are made in exactly one place, guaranteeing explainability and consistency.

## The "One Brain" Principle

In standard CLI tools, logic often leaks into the CLI layer or is scattered across multiple service classes. Gitv strictly prohibits this. 

The **Decision Engine** is the sole source of truth for planning. The CLI commands (like `gitv go` or `gitv sync`) do not contain workflow logic; they merely invoke the Decision Engine and pass the resulting plan to the **Execution Engine**.

## High-Level Architecture Flow

1. **SignalLayer:** Reads the raw Git state and translates it into standardized signals (e.g., `UNMERGED_PATHS`, `BEHIND_REMOTE`).
2. **RepoContext:** Builds an immutable snapshot of the repository state based on the signals.
3. **DecisionEngine & RuleAggregator:** Analyzes the `RepoContext` and determines the logical next steps.
4. **PlanBuilder:** Constructs a deterministic, ordered list of actions to execute.
5. **SafetyValidator:** Performs a final check on the plan to ensure no destructive actions are scheduled unintentionally.
6. **ExecutionEngine:** Executes the approved plan, handling standard output and error states.

## Why Deterministic Planning?

By strictly separating planning from execution, Gitv guarantees:
- **Explainability:** We can always print the plan before running it.
- **Safety:** The SafetyValidator can catch dangerous action combinations *before* any state changes.
- **Testability:** We can unit test the Decision Engine entirely without invoking actual Git processes.
