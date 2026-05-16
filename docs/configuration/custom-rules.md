# Custom Rules

Gitv's **Decision Engine** evaluates a series of rules to determine the workflow plan. While the core rules cover most standard Git workflows, you can define custom rules to handle specialized repository requirements.

## What is a Rule?

A Rule is a logical condition paired with an action. The **RuleAggregator** evaluates rules against the current `RepoContext`.

## Defining Project-Specific Rules

You can enforce specific behaviors by defining rules in your repository's local configuration. 

For example, to prevent direct pushes to the `main` branch, ensuring everyone uses Pull Requests:

```toml
# .gitv/config
[rules.protect-main]
condition = "branch == 'main' && action == 'push'"
enforce = "block"
message = "Direct pushes to main are not allowed. Please create a feature branch."
```

## Creating Custom Aliases

You can map specific workflow plans to custom Gitv commands.

```toml
# .gitv/config
[aliases]
ship-it = "go --apply --push"
save-point = "commit -m 'wip: saving state'"
```

*Note: Custom rules are currently considered an experimental feature and the syntax is subject to change.*
