# Prerequisites

Before installing Gitv, ensure your system meets the following requirements. Gitv is designed to be lightweight, but it relies on an underlying Git installation to function.

## Required Software

- **Git:** Gitv requires a standard Git installation.
  - **Minimum Version:** `2.20.0` or higher is recommended for full compatibility with all workflow features.
  - Check your version: `git --version`

## Operating System Support

Gitv provides pre-compiled native binaries for the following platforms:

- **macOS:** Intel (`x86_64`) and Apple Silicon (`arm64`)
- **Linux:** `x86_64` and `aarch64`
- **Windows:** `x86_64` (Available via Scoop or manual download)

## Network Requirements

If you plan to interact with remote repositories (e.g., GitHub, GitLab, Bitbucket), ensure you have standard SSH or HTTPS access configured for Git. Gitv utilizes your existing Git credential manager and SSH keys without requiring separate configuration.
