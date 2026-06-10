#!/bin/bash
# Android Deployment Script
set -euo pipefail

echo "🏗️ Building Android APK..."
./gradlew :packages:app:app:assembleRelease

APK_PATH="packages/app/app/build/outputs/apk/release/app-release.apk"
DEST="Kilo-Android-App.apk"

if [ -f "$APK_PATH" ]; then
    cp "$APK_PATH" "$DEST"
    echo "✅ Artifact created: $DEST"
else
    echo "❌ APK not found at $APK_PATH"
    exit 1
fi
