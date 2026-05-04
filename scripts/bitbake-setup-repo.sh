#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
bitbake_dir="$repo_root/layers/bitbake"

# Keep a dedicated BitBake checkout for the bitbake-setup tool itself.
# kas release builds use their own per-release checkouts under layers/<release>/.
if [[ -d "$bitbake_dir/.git" ]]; then
    current_branch="$(git -C "$bitbake_dir" rev-parse --abbrev-ref HEAD)"
    if [[ "$current_branch" == "2.18" ]]; then
        echo "bitbake repository already present at '$bitbake_dir' on branch 2.18."
        exit 0
    fi
    echo "bitbake repository at '$bitbake_dir' is on branch '$current_branch', switching to 2.18..."
    git -C "$bitbake_dir" fetch --depth 1 origin 2.18
    git -C "$bitbake_dir" checkout -B 2.18 FETCH_HEAD
    exit 0
fi

echo "Cloning bitbake (tooling checkout) into '$bitbake_dir'..."
mkdir -p "$(dirname "$bitbake_dir")"
git clone --depth 1 --branch 2.18 https://git.openembedded.org/bitbake "$bitbake_dir"

echo "Done. bitbake and bitbake-setup are now available in '$bitbake_dir/bin'."
