# Installation

Gitv provides multiple installation methods depending on your operating system. Choose the one that best fits your environment.

## macOS

The easiest way to install Gitv on macOS is using Homebrew:

```bash
brew tap V-Bach/gitv
brew install gitv
```

## Windows

For Windows users, we recommend using Scoop:

```powershell
scoop bucket add gitv https://github.com/V-Bach/gitv.git
scoop install gitv
```

## Linux & Universal Install Script

For Linux, or as a quick install on any Unix-like system, use our universal installation script. This script automatically detects your OS and architecture, downloading the correct native binary.

```bash
curl -fsSL https://raw.githubusercontent.com/V-Bach/gitv/main/install.sh | bash
```

## Manual Installation

If you prefer not to use a package manager, you can download the pre-compiled binaries directly from the [GitHub Releases page](https://github.com/V-Bach/gitv/releases).

1. Download the archive for your system.
2. Extract the binary.
3. Move it to a directory in your system's `PATH` (e.g., `/usr/local/bin` or `C:\Windows\System32`).

## Verifying the Installation

After installing, verify that Gitv is correctly added to your path by running:

```bash
gitv --version
```
