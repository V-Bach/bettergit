class Gitv < Formula
  desc "Autonomous Git workflow engine"
  homepage "https://github.com/V-Bach/bettergit"
  version "0.9.1"

  if OS.mac?
    if Hardware::CPU.arm?
      url "https://github.com/V-Bach/bettergit/releases/download/v0.9.1/gitv-darwin-arm64.tar.gz"
      sha256 "26aab502ad2cec8fb1822fc33332acf0867100f225280eb80fcef85dbb45d4e1"
    else
      odie "Intel Macs are not supported in this release."
    end
  elsif OS.linux?
    url "https://github.com/V-Bach/bettergit/releases/download/v0.9.1/gitv-linux-amd64.tar.gz"
    sha256 "1bc5784abdd0e96f9224e2963570c54ed9ad1220fdc74749ac0e28eb8ed94d15"
  end

  def install
    if OS.mac?
      bin.install "gitv-darwin-arm64" => "gitv"
    elsif OS.linux?
      bin.install "gitv-linux-amd64" => "gitv"
    end
  end

  test do
    system "#{bin}/gitv", "--version"
  end
end
