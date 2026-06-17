# Dice Rollar

A fast, no-nonsense Android dice roller for tabletop RPGs, board game nights, and any moment you need a quick fair number. Local-only, offline-first, no ads, no tracking.

## Features

- 7 dice types: d4, d6, d8, d10, d12, d20, d100
- Multi-die roller — any combination at once (e.g. 4d6, 2d20+5)
- Modifier stepper for quick +/- adjustments
- Full roll history persisted on device
- Dark theme designed for low-light tables
- No ads, no tracking, no third-party SDKs
- Around 3 MB installed
- Fully offline — no permissions, no network

## Build

```bash
# Debug
./gradlew assembleDebug

# Release APK (signed, requires keystore.properties)
./gradlew assembleRelease

# Release AAB for Play Console
./gradlew bundleRelease
```

Outputs:
- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Persistence:** DataStore (for roll history + preferences)

## Project layout

```
app/src/main/java/com/radhaarc/dicerollar/
├── domain/   # Dice models, roll logic, pure Kotlin core
├── data/     # DataStore-backed repositories (history, prefs)
├── ui/       # Compose screens, theme, ViewModels
└── MainActivity.kt
```

## Store assets

Play Console listing assets and screenshot instructions live under `store/`.

## License

© 2026 RadhaArc. All rights reserved.
