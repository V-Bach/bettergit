# Configuration Overview

Gitv is designed to work beautifully out-of-the-box with zero configuration. However, as your workflows become more complex, you may want to tune its behavior.

Gitv uses a hierarchy of configuration sources to determine its settings. You can configure execution modes, safety limits, custom aliases, and behavior overrides.

## Where is Configuration Stored?

Gitv reads configuration from three primary locations:

1. **Global/User Config:** `~/.gitvconfig`
   Affects all repositories for the current user.
2. **Repository Config:** `.gitv/config` (inside your project directory)
   Affects only the current repository. Useful for team-specific rules.
3. **Environment Variables:** `GITV_*`
   Useful for CI/CD pipelines and temporary overrides.

## Configuration Format

Configuration files use a simple TOML-like structure (or standard Git config structure, depending on implementation). 

*See the [Precedence Order](precedence-order.md) guide to understand how Gitv resolves conflicts between configuration layers.*
