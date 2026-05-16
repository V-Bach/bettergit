# Release Pipeline

Gitv uses a fully automated release pipeline powered by GitHub Actions. This ensures consistent, reproducible builds across all target operating systems.

## The GitHub Actions Workflow

When a new Release is tagged in the repository (e.g., `v1.2.0`), the `.github/workflows/release.yml` workflow triggers.

### Build Matrix

The workflow uses a matrix strategy to concurrently build native binaries for:
- Ubuntu (Linux `x86_64`)
- macOS (Intel `x86_64` and Apple Silicon `arm64`)
- Windows (`x86_64`)

### Pipeline Steps

For each platform, the pipeline:
1. Checks out the code.
2. Sets up the appropriate GraalVM environment.
3. Executes the Maven native build profile (`mvn package -Pnative`).
4. Generates cryptographic checksums (SHA-256) for the resulting binary.
5. Uploads the binary and checksums as artifacts to the GitHub Release.

## Package Manager Updates

Once the binaries are built and attached to the Release, the pipeline triggers downstream jobs to update distribution channels:
- It pushes an update to the Homebrew Tap repository with the new version and macOS/Linux checksums.
- It updates the Scoop bucket manifest with the new Windows checksum.
