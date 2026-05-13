#!/usr/bin/env bash
set -e

REPO="V-Bach/bettergit"

# Try to get the latest release tag. If it fails (e.g., only pre-releases exist), fallback to hardcoded.
VERSION=$(curl -sL "https://api.github.com/repos/$REPO/releases/latest" | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/')
if [ -z "$VERSION" ] || [ "$VERSION" == "null" ]; then
    VERSION="v0.9.0-rc1"
fi

OS="$(uname -s)"
ARCH="$(uname -m)"

case "${OS}" in
    Linux*)     OS_NAME="linux";;
    Darwin*)    OS_NAME="darwin";;
    *)          echo "Unsupported OS: ${OS}"; exit 1;;
esac

case "${ARCH}" in
    x86_64*)    ARCH_NAME="amd64";;
    aarch64*|arm64*) ARCH_NAME="arm64";;
    *)          echo "Unsupported architecture: ${ARCH}"; exit 1;;
esac

ASSET_NAME="gitv-${OS_NAME}-${ARCH_NAME}.tar.gz"
DOWNLOAD_URL="https://github.com/${REPO}/releases/download/${VERSION}/${ASSET_NAME}"
INSTALL_DIR="${HOME}/.local/bin"

echo "Downloading Gitv ${VERSION} for ${OS_NAME}-${ARCH_NAME}..."
curl -f -L -o gitv.tar.gz "${DOWNLOAD_URL}" || { echo "Download failed!"; exit 1; }

echo "Extracting..."
tar -xzf gitv.tar.gz

echo "Installing to ${INSTALL_DIR}..."
mkdir -p "${INSTALL_DIR}"
mv "gitv-${OS_NAME}-${ARCH_NAME}" "${INSTALL_DIR}/gitv"
chmod +x "${INSTALL_DIR}/gitv"
rm gitv.tar.gz

echo "====================================="
echo "Gitv installed successfully!"
echo "Location: ${INSTALL_DIR}/gitv"
echo "====================================="

if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo "WARNING: ${INSTALL_DIR} is not in your PATH."
    echo "Please add the following line to your ~/.bashrc or ~/.zshrc:"
    echo "export PATH=\"\$HOME/.local/bin:\$PATH\""
fi
