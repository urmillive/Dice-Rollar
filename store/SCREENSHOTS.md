# Dice Rollar — Play Store Screenshots

## Requirements

- **Minimum:** 2 phone screenshots
- **Ideal:** 4-6 phone screenshots
- **Resolution:** 1080x1920 or 1080x2400 (portrait)
- **Format:** PNG or JPEG, no alpha
- **Save to:** `store/screenshots/`

## Suggested shots

1. **`01-main.png`** — Main screen with multi-dice picker visible (d4/d6/d8/d10/d12/d20/d100 chips and the modifier stepper)
2. **`02-result.png`** — Result card mid-roll showing a big number, total + per-die breakdown
3. **`03-history.png`** — History list scrolled with several past rolls visible
4. **`04-crit.png`** — Result card showing a natural 20 / critical hit moment (orange accent celebration state)

Optional extras:
5. **`05-multi-roll.png`** — Rolling 4d6+2 type combo
6. **`06-dark-theme.png`** — Showcasing the dark theme across the app

## Capture methods

### Method A — adb (recommended, exact resolution control)

```bash
# Single shot
adb shell screencap -p /sdcard/screen.png && \
  adb pull /sdcard/screen.png ./store/screenshots/01-main.png

# Repeat for each screen, renaming as you go
adb shell screencap -p /sdcard/screen.png && \
  adb pull /sdcard/screen.png ./store/screenshots/02-result.png
```

Make sure the emulator AVD is configured at 1080x1920 or 1080x2400 (Pixel 5/6/7 profiles work).

### Method B — Android Studio emulator camera icon

1. Launch the emulator
2. Click the camera icon in the side toolbar
3. Screenshots save to `~/Pictures/Android-Emulator/`
4. Move/rename into `store/screenshots/`

## After capture

Verify dimensions:
```bash
identify store/screenshots/*.png
```

Each should report 1080x1920 or 1080x2400. If wrong, retake at correct emulator resolution rather than upscaling.
