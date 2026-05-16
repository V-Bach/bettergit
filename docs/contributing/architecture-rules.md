# Architecture Rules

Gitv's stability relies on strict architectural discipline. When contributing, you must adhere to these rules. PRs violating these principles will not be accepted.

## 1. The "One Brain" Principle
**There is no second Decision Engine.** 
All workflow planning logic must reside entirely within the `DecisionEngine` and `RuleAggregator`. 
- You may NOT add `if/else` statements regarding Git state in the CLI layer.
- You may NOT add planning logic in the Execution Engine.

## 2. No Logic Leakage into CLI
The CLI classes (e.g., the commands parsing user input) are extremely thin. They only translate flags into requests for the Decision Engine and pass the resulting plans to the Execution Engine. They must never inspect the `RepoContext` themselves.

## 3. The SafetyValidator Cannot Think
The `SafetyValidator` is a strict guardrail. It takes a `Plan` and either returns `true` or throws an exception.
- It is **not allowed** to add, remove, or reorder steps.
- It is **not allowed** to fix a bad plan; it must reject it.

## 4. ExecutionEngine is Orchestration Only
The `ExecutionEngine` loops through a `Plan` and executes the steps via `GitService`. It must never attempt to recover from an error by inventing new steps. If a step fails, the Execution Engine halts and reports the failure.

## 5. Context is Immutable
Once the `RepoContext` is built by the `SignalLayer` for a given execution run, it is immutable. Do not attempt to update the context mid-flight. If the state changes, a new execution pass must read a fresh context.
