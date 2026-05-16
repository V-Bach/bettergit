# Project Structure Awareness

Gitv is designed to be aware of your project's structure, allowing it to tailor its behavior based on the type of repository you are working in.

## Workspace Detection

Gitv scans the root of your repository to detect common build tools and project layouts:
- `package.json` (Node.js)
- `pom.xml` / `build.gradle` (Java)
- `Cargo.toml` (Rust)
- `go.mod` (Go)

## Intelligent Workflows

When Gitv detects specific project structures, it can adapt its workflows. For example, if configured to do so, it might warn you if you are attempting to publish changes without running the associated linter or test suite for that project type.

## Configuring Structure Ignorance

If Gitv's project structure awareness is overly aggressive for your specific setup, you can disable it in the configuration:

```toml
# .gitv/config
[context]
detect_project_type = false
```

## Monorepo Support

Gitv provides beta support for monorepos. By identifying the boundaries of internal packages, Gitv can provide more localized `status` outputs, reducing noise from untouched modules within a massive repository.
