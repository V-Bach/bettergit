# Contributing to Gitv

Thank you for your interest in contributing to **Gitv**

This document provides guidelines and instructions to help you contribute to the project as easily and effectively as possible.

## 1. Prerequisites
Gitv is written in Java and uses GraalVM to compile into a native executable.
To start developing, you need to install:
- **Java Development Kit (JDK) 17** (GraalVM JDK 17 is recommended if you want to build native executables).
- **Maven** (Version 3.8+). The project includes the Maven Wrapper (`mvnw`), so you can use it directly without a global Maven installation.
- **Git** (Of course!).

## 2. Local Setup

1. **Fork the repository** to your GitHub account.
2. **Clone the repository** from your fork:
   ```bash
   git clone https://github.com/V-Bach/bettergit.git
   cd bettergit
   ```
3. **Install dependencies and build the project:**
   ```bash
   ./mvnw clean install
   ```

## 3. Architecture Overview

To contribute effectively, here is a brief overview of the Gitv architecture:
- **`gitv.cli`**: Contains classes for handling the Command-line Interface, utilizing the **Picocli** library.
- **`gitv.engine`**: Contains `ExecutionPlan` and `SafetyValidator`. This is where the sequence of safe operations to be executed is determined.
- **`gitv.git`**: The layer that communicates directly with the Git CLI (`GitService`), executing commands and parsing the results.
- **`gitv.suggestion` / `gitv.workflow`**: The **Acyclic Strategy Graph** system that analyzes the repository state (`ContextBuilder`) and makes rational action decisions (such as `ActionKey.ADD`, commit, push, stash...).

**Development Philosophy:** Gitv prioritizes safety above all else. We do not arbitrarily alter complex Git history or automatically run `git commit -a`. Every operation must pass through the `SafetyValidator` before actual execution.

## 4. Development Workflow

1. **Branching:**
   Always create a new branch for your feature or bug fix.
   ```bash
   git checkout -b feature/amazing-feature
   # or
   git checkout -b fix/issue-name
   ```

2. **Writing Code:**
   - Ensure you follow the existing project structure.
   - Avoid introducing redundant code or unnecessary dependencies.
   - Add comments to complex logic blocks.

3. **Local Testing:**
   Test Gitv locally with the code you just modified:
   ```bash
   ./mvnw clean compile exec:java -Dexec.mainClass="gitv.cli.GitvRootCmd"
   ```
   If you want to test the Native Build:
   ```bash
   ./mvnw clean package -Pnative
   ./target/gitv
   ```

4. **Committing:**
   Please use **Conventional Commits** for your commit messages:
   - `feat: add new feature`
   - `fix: resolve issue ...`
   - `docs: update documentation`
   - `refactor: optimize source code`
   - `chore: update dependencies, build process...`

   Example:
   ```bash
   git commit -m "feat: add stash support in ModuleIntent"
   ```

5. **Pushing and Creating a Pull Request:**
   ```bash
   git push origin feature/amazing-feature
   ```
   Open a Pull Request (PR) on the original repository (`V-Bach/bettergit`). In the PR, clearly describe your changes and the reasoning behind them.

## 5. Reporting Bugs & Feature Requests
If you don't have the time to write code, you can still contribute by opening **Issues**:
- Describe the bug you encountered in detail (including OS, Git version, and the error output).
- For new features, explain a specific use case detailing why Gitv should have that feature.

---
Once again, thank you for taking the time to contribute to Gitv!
