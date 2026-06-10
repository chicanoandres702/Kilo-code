#!/bin/bash
# [Parent Feature/Milestone] Kilo Android App
# [Subtask] Download latest Kilo arm64 binary
# [Law Check] 20 lines

set -e

KILE_VERSION=$(curl -s https://api.github.com/repos/Kilo-Org/kilocode/releases/latest | jq -r .tag_name)
echo "Latest Kilo version: $KILE_VERSION"

ASSET_URL="https://github.com/Kilo-Org/kilocode/releases/download/${KILE_VERSION}/kilo-linux-arm64.tar.gz"
TMP_DIR="/tmp/kilo-download"
OUT_FILE="src/features/kilo-android/assets/kilo"

mkdir -p "$TMP_DIR"
curl -L "$ASSET_URL" -o "$TMP_DIR/kilo.tar.gz"
tar -xzf "$TMP_DIR/kilo.tar.gz" -C "$TMP_DIR"
cp "$TMP_DIR/kilo-linux-arm64/kilo" "$OUT_FILE"
chmod +x "$OUT_FILE"

echo "Kilo binary saved to: $OUT_FILE"
rm -rf "$TMP_DIR"