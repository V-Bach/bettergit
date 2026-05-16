# Remote Configuration

Gitv automatically detects and works with your existing Git remotes. You rarely need to configure remotes specifically for Gitv, but it provides options to handle complex multi-remote setups.

## Default Remote Resolution

When calculating whether your branch is ahead or behind, Gitv's **ContextBuilder** checks:
1. The remote tracking branch specifically configured for your current branch.
2. If none is set, it defaults to `origin`.

## Overriding the Default Remote

If your primary remote is named something other than `origin` (e.g., `upstream`), or if you are working in a fork-and-pull workflow, you can tell Gitv which remote is the source of truth for synchronization:

```toml
# .gitv/config
[sync]
default_remote = "upstream"
```

## Handling Multiple Remotes

In open-source workflows where you have an `origin` (your fork) and an `upstream` (the main repository), Gitv will detect this pattern. When running `gitv sync`, it will typically fetch from `upstream` to keep your branch current, but push to `origin`.
