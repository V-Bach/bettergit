const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const os = require('os');

const VERSION = 'v0.9.0';
const REPO = 'V-Bach/bettergit';

const platform = os.platform();
const arch = os.arch();

let osName = '';
if (platform === 'linux') osName = 'linux';
else if (platform === 'darwin') osName = 'darwin';
else if (platform === 'win32') osName = 'windows';
else {
  console.error(`Unsupported platform: ${platform}`);
  process.exit(1);
}

let archName = '';
if (arch === 'x64') archName = 'amd64';
else if (arch === 'arm64') archName = 'arm64';
else {
  console.error(`Unsupported architecture: ${arch}`);
  process.exit(1);
}

// In NPM packages, postinstall scripts run in the package root.
const binDir = path.join(__dirname, 'bin');
if (!fs.existsSync(binDir)) {
  fs.mkdirSync(binDir, { recursive: true });
}

// Windows
if (osName === 'windows') {
  const asset = `gitv-windows-amd64.zip`;
  const url = `https://github.com/${REPO}/releases/download/${VERSION}/${asset}`;
  const zipPath = path.join(__dirname, asset);
  
  console.log(`Downloading Gitv from ${url}...`);
  execSync(`powershell -Command "Invoke-WebRequest -Uri '${url}' -OutFile '${zipPath}'"`, { stdio: 'inherit' });
  
  console.log(`Extracting...`);
  execSync(`powershell -Command "Expand-Archive -Path '${zipPath}' -DestinationPath '${__dirname}' -Force"`, { stdio: 'inherit' });
  
  const exePath = path.join(__dirname, 'gitv-windows-amd64.exe');
  const targetPath = path.join(binDir, 'gitv.exe');
  
  if (fs.existsSync(exePath)) fs.renameSync(exePath, targetPath);
  if (fs.existsSync(zipPath)) fs.unlinkSync(zipPath);
} 
// Unix (Mac/Linux)
else {
  const asset = `gitv-${osName}-${archName}.tar.gz`;
  const url = `https://github.com/${REPO}/releases/download/${VERSION}/${asset}`;
  const tarPath = path.join(__dirname, asset);
  
  console.log(`Downloading Gitv from ${url}...`);
  execSync(`curl -f -L -o "${tarPath}" "${url}"`, { stdio: 'inherit' });
  
  console.log(`Extracting...`);
  execSync(`tar -xzf "${tarPath}" -C "${__dirname}"`, { stdio: 'inherit' });
  
  const extractedBin = path.join(__dirname, `gitv-${osName}-${archName}`);
  const targetPath = path.join(binDir, 'gitv');
  
  if (fs.existsSync(extractedBin)) {
    fs.renameSync(extractedBin, targetPath);
    fs.chmodSync(targetPath, 0o755);
  }
  if (fs.existsSync(tarPath)) fs.unlinkSync(tarPath);
}

console.log('Gitv native binary successfully installed via NPM!');
