# DiceRollar — Product Audit

**Date:** 2026-06-20
**Source of truth:** `/Users/home/Projects/Mobile Apps/Dice-Rollar/` (note: capitalized + hyphenated, not `dicerollar`)
**Lens:** Indie/solo founder, ship-to-revenue

---

## 1. Executive Verdict

**Ship after fixes — but as a portfolio piece, not a product attempt.**

The app is genuinely well-built (clean MVVM, DataStore, deterministic dice logic, Compose UI, no permissions, tests for the roll core) but it is undifferentiated in a category where the #1 free competitor (RPG Simple Dice) has 3.4M+ installs and is also free with no ads. Worse, the current UI ships a strict subset of what the store listing promises — there's no modifier stepper, no history screen, and no multi-die-type-in-one-roll. Either fix the listing to match the build, or fix the build to match the listing. Then ship for resume/portfolio credit, not revenue.

**Two pre-existing-belief corrections from this audit:**
1. The memory note from 2026-06-10 said a signed AAB was staged. **No AAB exists in `app/build/outputs/bundle/release/` right now** — only a debug APK. You'll need to re-run `./gradlew bundleRelease` before upload.
2. The README and store listing **promise features the app does not currently ship** (modifier stepper, viewable roll history).

---

## 2. Product Summary (what's actually built)

Verified by reading the Kotlin source under `app/src/main/java/com/radhaarc/dicerollar/`:

- **7 die types**: D4/D6/D8/D10/D12/D20/D100 (`domain/Die.kt`)
- **Single die-type per roll, count 1–12** — user picks a die type in Settings, then on the Roll screen taps +/- to set how many dice of that type to roll. Tapping anywhere on the dice area rolls them (`ui/roll/RollScreen.kt`, `ui/roll/RollViewModel.kt`)
- **Roll animation** — 650ms fake "rolling" delay then result reveal (`RollViewModel.roll()`)
- **Result display** — large pip-face dice grid + a "TotalPill" at the top showing sum. No per-die breakdown visible, no modifier shown
- **Settings screen** — toggle same-color-all-dice, toggle borders, choose Dots vs Numbers display, change active die type (`ui/settings/SettingsScreen.kt`)
- **Persistence** — DataStore-backed `SettingsStore` + `HistoryStore` keeping last 20 rolls (`data/HistoryStore.kt`). **`HistoryStore` is wired up and writes on every roll, but there is no UI to view history.** Dead code path from a user POV.
- **Modifier** — `RollRequest` accepts a modifier (-99..+99) and `RollResult.notation` formats `2d20+5`, but `RollViewModel.roll()` hardcodes `modifier = 0`. The feature exists in the domain layer; **no UI exposes it**.

### Listing-vs-reality gap (P0)

| Listing claim | Reality |
|---|---|
| "Multi-die roller — any combination at once (e.g. 4d6, 2d20+5)" | Single die type only. Can't combine d20+d6 in one roll. |
| "Modifier stepper for quick +/- adjustments" | No modifier UI. Hardcoded to 0. |
| "Full roll history persisted on device" | Persisted, yes. No way to view it in the app. |

Shipping the current build with the current listing description is a Play Store policy risk (misleading description) and a 1-star review magnet ("where's the history?").

---

## 3. Market Analysis

### Top dice-roller competitors on Play Store

1. **RPG Simple Dice (ToolboxDev)** — ~3.4M installs, 4.49 stars, free with ads. Custom dice, modifiers, history, "armory" for saved rolls. The category leader.
2. **Dice Roller! (harryfo)** — fast custom dice up to d100, board games + RPGs framing. Long-standing app.
3. **DnDice — 3D RPG Dice Roller** — 3D physics rendering, D&D-skinned. The "physics" wedge is already taken.
4. **Dice Roller: Shake & Roll Dice (sampartridge)** — board game framing (Monopoly, Yahtzee, Cluedo), shake-to-roll. The "board games" wedge.
5. **Roll My Dice: Custom Dice (Wychway Studios)** — custom dice faces, the "make your own" wedge.

### Reality check

- The top 3 free dice apps are well-entrenched, each with multiple years of reviews and SEO authority.
- The category is saturated: probably 200+ dice rollers on Play. Most are free with ads. Almost none make meaningful indie revenue.
- DiceRollar's only build-time wedges are: (a) zero permissions, (b) zero ads, (c) ~3MB install size. These are nice but not searchable — nobody searches "dice roller no ads" enough to be a wedge.

### Wedge candidates (if you decide to make this a real product)

None of these are in the current build. Pick at most one:

- **TTRPG notation parser** — type `4d6kh3+2` (keep highest 3) or `2d20kh1` (advantage). Real D&D players want this. The domain has 80% of what's needed already (`RollResult.notation` formats it; just needs parser + drop/keep logic).
- **Multi-die-type single roll** — "Attack: 1d20+5, on hit Damage: 2d6+3" saved as a one-tap macro. This is the "armory" feature RPG Simple Dice already has, so you'd be matching not differentiating.
- **Share-to-stream / OBS overlay** — generate a shareable URL/image of the roll for D&D streamers. Niche but underserved.
- **Voice rolling** — "roll 4d6 keep highest 3" via speech. Hands-free while DMing. Genuinely new.

**If you can't commit to one of these, recommendation is Section 1's verdict: ship as portfolio filler, don't try to grow it.**

---

## 4. Play Store Launch Readiness

| Item | Status |
|---|---|
| App code complete | Yes (for what's in the listing-minus-promised-features) |
| Signed AAB present | **No** — `app/build/outputs/bundle/release/` is missing. Only `apk/debug/app-debug.apk` exists. **Run `./gradlew bundleRelease`** |
| Upload keystore present | Yes — `app/upload-keystore.jks` |
| `keystore.properties` configured | Inferred yes (build.gradle reads from `rootProject.file("keystore.properties")`) — verify it's not missing from gitignore |
| Icon 512×512 | Yes — `store/icon-512.png` (but `play-store/content-form-answers.md` notes icon is a "placeholder d20 vector — needs design pass before Production") |
| Feature graphic 1024×500 | Yes — `store/feature-graphic-1024x500.png` |
| Phone screenshots (min 2) | **No** — `store/screenshots/` does not exist. SCREENSHOTS.md has the capture plan only |
| Privacy policy hosted | **Yes** — verified live at https://urmillive.github.io/dicerollar-privacy/ |
| Data safety form ready | Yes — all "no" answers, drafted in SUBMISSION-CHECKLIST.md and content-form-answers.md |
| Content rating | Drafted — Everyone (or 13+ per content-form-answers, 18+ per SUBMISSION-CHECKLIST — **these two files disagree**, pick one) |
| Store listing copy | Yes — `store/listing.md`, but description over-promises features that aren't in build |
| Internal testing track plan | Documented |

### Inconsistencies between submission docs to resolve

- **Age target**: SUBMISSION-CHECKLIST says "18 and over"; content-form-answers says "13 and over". Pick 13+; it's accurate and avoids gating.
- **Content rating category**: SUBMISSION says "Reference, News, or Educational"; content-form says "Utility, Productivity, Communication, or Other". Pick the latter — this isn't a reference app.
- **AdMob "Phase 2"**: content-form-answers casually mentions adding AdMob later. If you go that route you have to redo the data safety form. Decide ad strategy now (see §6).

---

## 5. Gap Analysis

### P0 — Must fix before submit (3-4 days)

1. **Rebuild signed AAB** — `./gradlew bundleRelease`, verify file at `app/build/outputs/bundle/release/app-release.aab`
2. **Fix the listing-reality mismatch.** Two options:
   - **Option A (faster, recommended for portfolio play):** Rewrite `store/listing.md` to describe what actually ships: "Roll 1–12 dice of one type at a time. Pick your die (d4–d100), tap to roll. Tiny, offline, no ads." Drop the modifier and multi-combo claims. Drop the history claim until you add a screen.
   - **Option B (4 extra days):** Add a modifier stepper (the domain already supports it) and a history screen (HistoryStore already writes). Then keep the current listing.
3. **Capture screenshots** per `store/SCREENSHOTS.md` plan. Need at least 2, ideally 4.
4. **Reconcile submission docs** (age, category, ads decision).
5. **Audit icon**: content-form-answers flagged it as a placeholder — confirm `store/icon-512.png` is the final design before Production track.

### P1 — Month 1 if you commit to the wedge

- **If the wedge is TTRPG notation**: ship a parser-driven roll input (`2d20kh1+5`). Reuse `RollRequest` + `RollResult.notation` (already supports the format).
- **If the wedge is share-to-stream**: PNG export of the last roll + share-sheet integration.
- **History screen** (always P1, regardless of wedge — it's literally already persisted)
- **Modifier stepper** (always P1 — domain supports it)

### P2 — Nice to have

- Custom dice colors as a paid pack (only matters if you monetize)
- Roll macros / saved rolls ("Attack", "Damage", "Save")
- Home-screen widget for one-tap d20
- Haptics on roll resolve (low effort, real UX win)
- Sound effects toggle

---

## 6. Monetization Plan

**Recommendation: ship free, no ads, no IAP. Portfolio play.**

Why:

- Indie dice apps make near zero. Even the 3.4M-install category leader runs on ads, not a sustainable indie outcome.
- Adding AdMob (as currently hinted in content-form-answers.md) means: data-safety form changes, advertising ID disclosure, AdMob account setup, and adds ~$0–$5/month in revenue at realistic install volumes (~1–2k installs in year one for a no-marketing solo launch).
- The app's only real differentiator right now is "zero permissions, zero network." Adding AdMob actively destroys that wedge — and your privacy policy.

**Realistic monthly revenue band (free, no ads):** $0/mo. That's the point — this is a portfolio piece.

**If you must monetize:** $1.99 one-time "pro pack" with TTRPG notation + share-to-stream + custom skins. **Don't do hybrid free+ads**; it eats the differentiator and the revenue is rounding-error anyway.

---

## 7. Distribution & ASO

### Keywords (in priority order)

Broad (high competition, low conversion):
1. dice roller
2. dice
3. roll dice

Long-tail (lower volume, much better conversion):
4. d20 roller
5. dnd dice roller
6. dungeons dragons dice
7. tabletop dice
8. pathfinder dice roller
9. offline dice roller
10. simple dice roller no ads

### Title formula options

- **"Dice Rollar — Offline d20 & RPG Dice"** (best balance, hits "offline" + "d20" + "RPG" long-tails)
- **"Dice Rollar: d4 d6 d8 d20 d100"** (keyword-stuffed, ugly but searchable)
- **"Dice Rollar — Simple Dice Roller"** (clean, weak ASO)

Pick option 1.

### Short description (80 chars)

Current: "Roll d4 through d100. History, modifiers, dark theme. Offline. Free." (68/80) — **lies about history and modifiers**. Replace with:

"Offline d4–d100 dice roller. No ads, no permissions, 3MB. For D&D and RPGs." (76/80)

### Launch channels

- **r/dndnext** — be careful, self-promo is gated; share as "I built this, here's the source" with GitHub link
- **r/rpg** — Self-promotion Sundays only
- **r/Pathfinder_RPG** — same self-promo rules
- **r/androiddev** — "shipped my first Compose app" angle works here; this is probably the highest-yield channel for a portfolio play
- **EN World forums** — niche but engaged TTRPG audience
- **Hacker News Show HN** — only if you have a wedge. As-is, won't resonate.
- **r/AndroidGaming** — board games audience

For a portfolio play, **r/androiddev is the right channel**. The TTRPG subs are a waste unless you ship a wedge feature.

---

## 8. 2-Sprint Plan

### Sprint 0 — Pre-launch (3 working days)

| Day | Task |
|---|---|
| 1 | Rewrite `store/listing.md` to match actual build (Option A from P0). Reconcile age/category in submission docs. Confirm icon is final. |
| 2 | `./gradlew bundleRelease`. Capture 4 screenshots per SCREENSHOTS.md. Verify dimensions. |
| 3 | Play Console: create app, walk through all forms (15 min per checklist), upload AAB to Internal Testing track, add 2 tester emails, publish. |

### Sprint 1 — Post-launch (7 working days)

| Day | Task |
|---|---|
| 1 | Wait for pre-launch report. Fix any crashes flagged. |
| 2 | Promote to Production track. |
| 3-4 | r/androiddev post: "Shipped my Compose dice app, here's what I learned about Play Store submission." Link GitHub repo. |
| 5-7 | If decision is "portfolio only" → stop here, move on. If decision is "real product" → start on chosen wedge (TTRPG notation parser is highest-ROI). Add history screen + modifier stepper as parallel work since both are 80% done in the domain layer already. |

---

## 9. Risk Register

1. **Category saturation** — RPG Simple Dice (3.4M installs) and similar apps own the SEO. No realistic path to top 10 organic without a wedge. *Mitigation: accept portfolio framing.*
2. **Listing/build mismatch** — current listing promises features not in app. 1-star review risk, possible Play policy flag for misleading description. *Mitigation: rewrite listing before submit (P0).*
3. **Zero monetization** — even with AdMob, revenue is rounding error. Maintenance cost (Android target SDK bumps, Compose updates) probably > revenue forever. *Mitigation: explicitly scope as no-revenue portfolio asset; budget ~4 hours/year for SDK upgrades.*
4. **Native-Android-only TAM** — half the TTRPG mobile audience is on iOS. *Mitigation: not worth porting; KMP would be a rebuild for an already-low-ceiling category.*
5. **Maintenance debt** — Play Console will force a `targetSdk` bump every August. If you stop caring, the listing gets unpublished. *Mitigation: calendar reminder for annual maintenance, or accept that it gets archived after 2 years.*

---

## 10. Decision Asks (most important)

**The one question that determines everything:** Is DiceRollar a portfolio piece (ship-and-forget, prove you can ship a Play Store app end-to-end) or a real product attempt (commit to one wedge and grind)?

- **If portfolio piece:** Do Sprint 0 only. Rewrite the listing to be honest about what ships. Submit. Don't touch it again. ~$0 revenue, 1 line on resume saying "shipped native Android app on Play Store."
- **If real product:** Pick ONE wedge from §3 (recommend TTRPG notation parser — domain already supports it). Sprint 0 + Sprint 1 in full. Still expect ~$0 revenue but you'll have something defensible to point at on r/dndnext.

### Secondary asks

1. **AdMob: yes or no?** Recommend no — destroys the privacy wedge for ~$0 revenue.
2. **Age target: 13+ or 18+?** Recommend 13+ — it's accurate.
3. **Is the d20 icon truly final, or does it still need a design pass?** content-form-answers.md flagged it as a placeholder.

---

## Files referenced

- `README.md`
- `store/listing.md`
- `store/SUBMISSION-CHECKLIST.md`
- `store/SCREENSHOTS.md`
- `store/PRIVACY-POLICY.md`
- `play-store/content-form-answers.md`
- `play-store/privacy-policy.md`
- `app/build.gradle.kts`
- `app/src/main/java/com/radhaarc/dicerollar/MainActivity.kt`
- `app/src/main/java/com/radhaarc/dicerollar/domain/` (DiceRoller, Die, RollRequest, RollResult)
- `app/src/main/java/com/radhaarc/dicerollar/data/` (AppSettings, SettingsStore, HistoryStore)
- `app/src/main/java/com/radhaarc/dicerollar/ui/roll/RollScreen.kt`
- `app/src/main/java/com/radhaarc/dicerollar/ui/roll/RollViewModel.kt`
- `app/src/main/java/com/radhaarc/dicerollar/ui/settings/SettingsScreen.kt`
- `app/src/test/java/com/radhaarc/dicerollar/DiceRollerTest.kt`

Sources:
- [RPG Simple Dice on Play Store](https://play.google.com/store/apps/details?id=com.ccp.rpgsimpledice)
- [Dice Roller! (harryfo) on Play Store](https://play.google.com/store/apps/details?id=com.harryfo.dice)
- [DnDice Roller on Play Store](https://play.google.com/store/apps/details?id=com.mindflip.dndiceroller)
- [DiceRollar Privacy Policy (live)](https://urmillive.github.io/dicerollar-privacy/)
