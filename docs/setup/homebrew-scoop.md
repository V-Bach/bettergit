# Package Manager Distribution

Gitv aims for seamless installation across operating systems. We maintain automated distribution through Homebrew (macOS/Linux) and Scoop (Windows).

## Homebrew Tap (macOS/Linux)

Homebrew is the standard package manager for macOS. 

Instead of adding Gitv to the massive core Homebrew repository immediately, we maintain our own "Tap" (a dedicated repository containing formulas).

- **Tap Repository:** `V-Bach/homebrew-gitv` (Conceptual example based on install instructions)
- **Formula:** The `gitv.rb` Ruby script defines where to download the binary, verifies the SHA-256 checksum, and places the executable in the system path.

Our GitHub Actions release pipeline automatically generates a new `gitv.rb` formula and pushes it to the Tap repository whenever a new version is released.

## Scoop Bucket (Windows)

Scoop is a command-line installer for Windows that avoids permission dialogs and graphical installers.

Similar to Homebrew, we maintain a Scoop "Bucket" (a repository of JSON manifests).

- **Manifest:** The `gitv.json` file dictates the download URL for the Windows binary, its checksum, and how to expose it to the PowerShell environment.

The release pipeline updates this JSON manifest automatically on every release.
