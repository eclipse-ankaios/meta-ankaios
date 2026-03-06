#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
bitbake_dir="$repo_root/layers/bitbake"

if [[ -d "$bitbake_dir/.git" ]]; then
    echo "bitbake repository already present at '$bitbake_dir'."
    exit 0
fi

echo "Cloning bitbake into '$bitbake_dir'..."
mkdir -p "$repo_root/layers"
git clone --depth 1 --branch 2.16 https://git.openembedded.org/bitbake "$bitbake_dir"

echo "Done. bitbake and bitbake-setup are now available in '$bitbake_dir/bin'."
