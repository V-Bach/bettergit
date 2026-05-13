class Gitv < Formula
  desc "Autonomous Git workflow engine"
  homepage "https://github.com/V-Bach/bettergit"
  version "0.9.0-rc1"

  if OS.mac?
    if Hardware::CPU.arm?
      url "https://github.com/V-Bach/bettergit/releases/download/v0.9.0-rc1/gitv-darwin-arm64.tar.gz"
      # TODO: Run `shasum -a 256 gitv-darwin-arm64.tar.gz` and replace the hash below
      sha256 "REPLACE_WITH_ACTUAL_SHA256"
    else
      url "https://github.com/V-Bach/bettergit/releases/download/v0.9.0-rc1/gitv-darwin-amd64.tar.gz"
      # TODO: Run `shasum -a 256 gitv-darwin-amd64.tar.gz` and replace the hash below
      sha256 "REPLACE_WITH_ACTUAL_SHA256"
    end
  elsif OS.linux?
    url "https://github.com/V-Bach/bettergit/releases/download/v0.9.0-rc1/gitv-linux-amd64.tar.gz"
    # TODO: Run `shasum -a 256 gitv-linux-amd64.tar.gz` and replace the hash below
    sha256 "REPLACE_WITH_ACTUAL_SHA256"
  end

  def install
    if OS.mac?
      if Hardware::CPU.arm?
        bin.install "gitv-darwin-arm64" => "gitv"
      else
        bin.install "gitv-darwin-amd64" => "gitv"
      end
    elsif OS.linux?
      bin.install "gitv-linux-amd64" => "gitv"
    end
  end

  test do
    system "#{bin}/gitv", "--version"
  end
end
