<p align="center">
  <a href="https://github.com/V-Bach/bettergit">
    <picture>
      <source srcset="assets/logo-dark.svg" media="(prefers-color-scheme: dark)">
      <source srcset="assets/logo-light.svg" media="(prefers-color-scheme: light)">
      <img src="assets/logo-light.svg" alt="Gitv logo">
    </picture>
  </a>
</p>
<p align="center">The smart, safe, and automated Git workflow CLI.</p>

[![Gitv Terminal UI](assets/screenshot.png)](https://github.com/V-Bach/bettergit)

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License"></a>
  <a href="https://www.npmjs.com/package/gitv"><img src="https://img.shields.io/npm/v/gitv" alt="NPM Version"></a>
  <a href="https://brew.sh"><img src="https://img.shields.io/badge/homebrew-supported-orange.svg" alt="Homebrew"></a>
</p>

---

### Installation

```bash
# YOLO (Universal Shell Script for Linux / macOS / Windows Git Bash)
curl -sL https://raw.githubusercontent.com/V-Bach/bettergit/main/install.sh | bash

# Package managers
npm install -g @vuthebach/gitv-cli        # Node.js
brew tap V-Bach/gitv && brew install gitv # macOS and Linux
```

```powershell
# Windows (Scoop)
scoop bucket add gitv https://github.com/V-Bach/gitv-bucket.git
scoop install gitv
```

> [!TIP]
> Gitv treats your source code as sacred. It will only commit files you have explicitly staged (no automatic `git commit -a`).

### Quick Start

Inside any Git repository, simply run:

```bash
gitv
```

That's it. Gitv will analyze the repository context, determine what needs to be done (e.g., commit staged files, pull remote changes, push), and execute the plan safely.

### Features

- 🧠 **Context-Aware Routing:** Intelligently detects if you need to commit, pull, push, stash, or resolve conflicts.
- 🛡️ **Predictive Safety Validator:** Simulates operations before executing them to ensure your repository never ends up in a corrupted state.
- 🚦 **Rich, Readable UI:** Color-coded, emoji-free, professional terminal diagnostics that show exactly what’s happening.
- 🔄 **Resilient Crash Recovery:** Interrupt a sync? Gitv knows where it left off and safely resumes the operation.
- 🛑 **Zero Implicit Commits:** Never automatically stages all files. Gitv respects what you explicitly added to the index.

### Architecture & Philosophy

Gitv exists to collapse daily Git boilerplate into a single, intelligent command. Unlike traditional Git wrappers that blindly run `git commit -am`, Gitv uses a deterministic **Acyclic Strategy Graph** to understand your repository's exact state, predict the outcome of operations, and safely sync your work—all while respecting your explicit staging intent.

Instead of nested branching logic, Gitv translates repository state signals into a distinct `ModuleIntent`. This intent is then mapped to an `ExecutionPlan`—a linear sequence of isolated steps. Before anything runs, a `SafetyValidator` verifies the plan against current system constraints. This strict separation of decision-making, planning, and execution ensures that Gitv behaves predictably in every possible edge case.

### Documentation

Full documentation, including advanced configuration, CI/CD usage, and manual command overrides, will be available soon in our `docs/` directory.

### Contributing

We welcome contributions! Whether it's reporting a bug, proposing a new feature, or submitting a Pull Request, your help is appreciated. 

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### FAQ

#### How is this different from shell aliases?

Aliases are blind. They execute sequentially regardless of context. Gitv reads the repository context first to decide *which* commands are safe and necessary to run.

#### Will Gitv mess up my complex Git history?

No. Gitv only automates standard, safe Git operations. It does not perform complex history rewrites without explicit instruction.

#### Can I run Gitv in CI environments?

Yes. Gitv is designed to be headless-friendly with the appropriate flags for non-interactive automation.

---

Distributed under the MIT License. See `LICENSE` for more information.
