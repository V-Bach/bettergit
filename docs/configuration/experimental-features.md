# Experimental Features

Gitv constantly iterates on new ways to orchestrate workflows safely. We expose these features early as experimental flags so power users can provide feedback before the features become stable.

## Enabling Experimental Features

Experimental features are disabled by default to maintain the strict safety guarantees of Gitv's architecture. You can enable them globally or per-project.

```toml
# .gitv/config
[experimental]
monorepo_support = true
ai_commit_messages = false
```

## Current Experimental Features

### Monorepo Context (`monorepo_support`)
Enhances the `RepoContext` to understand subdirectory boundaries, allowing `gitv go` to operate only on the local module you are currently inside, rather than the entire repository.

### Auto-Resolution Engine (`auto_resolve`)
Allows the **SafetyValidator** to attempt automatic resolution of simple merge conflicts (e.g., whitespace-only conflicts) during a `sync` operation.

> **Warning:** Experimental features bypass some standard architectural guardrails. If Gitv crashes while using an experimental feature, please check the [Troubleshooting](../troubleshooting/failed-execution.md) documentation and report the issue on GitHub.
