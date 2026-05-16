# Adding Workflow Rules

The most common way to extend Gitv is by adding new workflow capabilities to the **Decision Engine**. This is done by adding new Rules.

## The Rule Interface

Rules typically implement a standard interface requiring two methods:
1. `evaluate(RepoContext context)`: Returns a boolean indicating if this rule applies.
2. `getAction()`: Returns the specific workflow step to append to the plan.

## Steps to Add a New Rule

1. **Understand the Signals:** Does your rule rely on existing Git state? If so, check the `RepoContext`. If the state isn't tracked, you must first update the `SignalLayer` to parse this new state from Git.
2. **Create the Rule Class:** Implement your rule logic, ensuring it clearly defines the specific conditions under which it triggers.
3. **Register the Rule:** Add your new rule to the `RuleAggregator` list. Pay attention to ordering, as rules are evaluated in priority order.
4. **Write Tests:** This is critical.
   - Write unit tests mocking the `RepoContext` to prove your rule triggers exactly when expected.
   - Write negative tests proving it does *not* trigger in unrelated states.

## Rule Guidelines

- **Keep Rules Isolated:** A rule should evaluate context independently. Do not create complex dependencies between rules.
- **Predictability:** Ensure your rule's condition doesn't overlap with an existing rule in a way that causes unpredictable planning behavior.
