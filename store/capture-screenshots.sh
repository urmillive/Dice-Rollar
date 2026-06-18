#!/usr/bin/env bash
# Dice Rollar — guided Play Store screenshot capture.
#
# Prereqs:
#   1. An Android emulator OR a USB-connected phone with USB debugging on
#   2. Dice Rollar installed and running on it
#      (Android Studio → Run → app on a connected device, OR
#       adb install app/build/outputs/apk/release/app-release.apk if you've assembled an APK)
#   3. adb on PATH (from $ANDROID_HOME/platform-tools)
#
# Usage:
#   bash store/capture-screenshots.sh

set -euo pipefail

STORE_DIR="$(cd "$(dirname "$0")" && pwd)"
OUT="$STORE_DIR/screenshots"
mkdir -p "$OUT"

if ! command -v adb >/dev/null 2>&1; then
  echo "✗ adb not found. Add \$ANDROID_HOME/platform-tools to PATH."
  exit 1
fi

DEVICES=$(adb devices | grep -E "device$" | wc -l | tr -d ' ')
if [ "$DEVICES" -eq 0 ]; then
  echo "✗ No Android devices/emulators detected. Boot one and rerun."
  exit 1
fi
echo "✓ $DEVICES device(s) connected"

capture() {
  local name="$1"
  local prompt="$2"
  echo ""
  echo "→ Set up screen: $prompt"
  read -r -p "  Press ENTER when ready (or 's' to skip)... " key
  if [ "$key" = "s" ]; then
    echo "  ⏭  skipped"
    return
  fi
  local path="$OUT/$name"
  adb shell screencap -p /sdcard/_capture.png
  adb pull /sdcard/_capture.png "$path" >/dev/null
  adb shell rm /sdcard/_capture.png
  local dims
  dims=$(file "$path" | grep -oE '[0-9]+ x [0-9]+' | head -1)
  echo "  ✓ saved $path ($dims)"
}

cat <<'BANNER'

╭─────────────────────────────────────────────────────────╮
│  Dice Rollar screenshot capture                         │
│  Capturing 5 shots. Min 2 required by Play Console.     │
╰─────────────────────────────────────────────────────────╯
BANNER

capture "01-main.png"          "Main roll screen with all 7 dice types visible (d4/d6/d8/d10/d12/d20/d100)"
capture "02-roll-result.png"   "Result card immediately after a roll — big number, dice breakdown"
capture "03-history.png"       "History list with 5-6 past rolls"
capture "04-multi-dice.png"    "Multi-dice config: e.g. 3d6 + 2 (use count stepper + modifier)"
capture "05-nat20.png"         "Roll a d20 until you get a 20 — show the critical-hit result card"

echo ""
echo "Done. Screenshots in $OUT:"
ls -la "$OUT"
echo ""
echo "Next: upload to Play Console → Main store listing → Phone screenshots."
