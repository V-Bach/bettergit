# Frequently Asked Questions

**Q: Does Gitv replace Git?**
No. Gitv is an orchestrator *for* Git. It uses the Git installation on your machine to perform all actions. It simplifies the workflow, but underlying Git mechanics remain the same.

**Q: Can I use Gitv and standard Git commands interchangeably?**
Yes. Because Gitv relies on standard Git state (the `.git` directory), you can use `gitv` commands and standard `git` commands side-by-side without issue. If you use standard Git to resolve a conflict, Gitv will recognize the resolution on its next run.

**Q: Why doesn't Gitv have a GUI?**
Gitv is designed to bring safety and explainability to the terminal. We believe that by removing the cognitive load of manual state management, the CLI becomes the fastest and most efficient way to interact with version control.

**Q: Will Gitv delete my uncommitted work?**
No. Gitv's **SafetyValidator** is designed specifically to protect uncommitted work. Operations that modify history or pull remote changes will automatically stash your work first, or refuse to run if stashing isn't safe.

**Q: How do I uninstall Gitv?**
If you used a package manager:
- macOS: `brew uninstall gitv`
- Windows: `scoop uninstall gitv`
If installed manually or via script, simply delete the `gitv` executable from your `PATH`.
