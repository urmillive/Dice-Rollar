#!/usr/bin/env bash
# Dice Rollar — sideload to a USB-connected Android phone and launch.
#
# Prereqs:
#   1. Phone has USB debugging on (Settings → About → tap Build number 7x, then
#      Settings → Developer options → USB debugging)
#   2. Phone connected via USB. First time, accept the RSA fingerprint prompt.
#   3. adb on PATH ($ANDROID_HOME/platform-tools).
#
# Usage:
#   bash store/sideload.sh

set -euo pipefail

STORE_DIR="$(cd "$(dirname "$0")" && pwd)"
APK="$STORE_DIR/sideload/dicerollar-1.0.0.apk"

if [ ! -f "$APK" ]; then
  echo "✗ APK missing at $APK"
  echo "  Regenerate with bundletool from the AAB."
  exit 1
fi

if ! command -v adb >/dev/null 2>&1; then
  echo "✗ adb not found. Add \$ANDROID_HOME/platform-tools to PATH."
  exit 1
fi

DEVICES=$(adb devices | grep -E "device$" | wc -l | tr -d ' ')
if [ "$DEVICES" -eq 0 ]; then
  echo "✗ No device connected. Plug in your phone with USB debugging on."
  exit 1
fi
echo "✓ $DEVICES device(s) connected"

echo "→ Uninstalling any existing Dice Rollar install (ignoring errors)..."
adb uninstall com.radhaarc.dicerollar >/dev/null 2>&1 || true

echo "→ Installing $APK ($(du -h "$APK" | cut -f1))..."
adb install -r "$APK"

echo "→ Launching..."
adb shell monkey -p com.radhaarc.dicerollar -c android.intent.category.LAUNCHER 1 >/dev/null

echo ""
echo "✓ Installed and launched. Now run: bash $STORE_DIR/capture-screenshots.sh"
