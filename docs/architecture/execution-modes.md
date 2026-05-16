# Execution Modes

Gitv supports different execution modes, allowing users to choose their preferred level of automation and interaction. The Execution Engine honors these modes during plan execution.

## The Three Modes

1. **AUTO**
   - Gitv runs the entire plan without prompting.
   - It only stops if an unexpected error or conflict occurs.
   - *Best for: Experienced users, CI environments, or routine syncs.*

2. **GUARDED**
   - Gitv automatically pauses before any potentially destructive action (like a rebase or a reset) and asks for user confirmation.
   - Non-destructive actions (like fetching or stashing) proceed automatically.
   - *Best for: Day-to-day workflows where users want oversight.*

3. **INTERACTIVE**
   - Gitv pauses and prompts for confirmation before *every single step* in the plan.
   - *Best for: Beginners, debugging, or complex, unfamiliar repository states.*

## Setting the Execution Mode

Execution modes can be set via configuration or temporarily overridden via CLI flags:

```bash
gitv go --mode=interactive
```
