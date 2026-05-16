# The Decision Engine

The **Decision Engine** is the heart of Gitv's intelligence. It evaluates the current `RepoContext` and outputs a deterministic plan of action.

## Core Philosophy: No Logic Leakage

The most strict architectural rule in Gitv is: **There is no second Decision Engine.**

- The CLI layer must never contain `if/else` logic regarding Git state.
- The Execution Engine must remain orchestration-only; it must never "decide" to run a command that isn't explicitly in the plan.
- The SafetyValidator is a guardrail, it is **not allowed to think** or alter the plan, it can only approve or reject.

## How it Works

The Decision Engine relies on the **RuleAggregator**. 

1. The Engine receives the `RepoContext`.
2. The `RuleAggregator` iterates through a series of defined workflow rules.
3. If a rule's condition matches the context, the rule provides an action step.
4. The Engine uses the **PlanBuilder** to assemble these steps logically.

## Example Reasoning

If the `RepoContext` shows:
- Uncommitted changes (True)
- Remote tracking branch exists (True)
- Local is behind Remote (True)

The Decision Engine reasons:
*I cannot pull because the working tree is dirty. I must first stash. Then I can pull via rebase to maintain a linear history. Finally, I must pop the stash.*

Resulting Plan: `STASH` -> `FETCH_REBASE` -> `STASH_POP`
