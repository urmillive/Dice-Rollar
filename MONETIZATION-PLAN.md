# Dice-Rollar — Monetization Plan

**Date:** 2026-06-24 · **Updated:** 2026-06-27 (gap-find pass)
**Branch:** `feat/monetization-plan`
**Target version:** 1.1.0 / versionCode 10 (skip 4–9 to leave room for hotfixes)
**Status:** ⬜ PLAN ONLY — no code in this branch yet.

---

## 0. Pre-flight (do this **before** M1)

These have lead times measured in days, not hours. Start them in parallel with M1 — if any is missing on M5/release day, the launch slips.

| Item | Owner action | Lead time | Blocks |
|---|---|---|---|
| **Play merchant account** | Play Console → Setup → Payments profile → create | 1–3 days | All IAP work |
| **Tax info** | Payments profile → tax form (W-8BEN for non-US individuals) | Same form | First payout |
| **Bank account verification** | Payments profile → bank → micro-deposit verify | 3–5 business days | First payout |
| **License tester accounts** | Play Console → Setup → License testing → add Gmail addresses (your dev account + a backup) | Minutes | Real billing in internal track |
| **AdMob account + Play link** | apply at admob.google.com, then link the Play Store app inside AdMob | 1–2 days approval | Real ad unit IDs (M6) |
| **Promo codes earmarked** | Note: Play gives 500 one-time-product promo codes per quarter — reserve ~20 for reviewers/friends | n/a | Free Pro grants without real charges |
| **Privacy policy hosting decided** | Pick one: GitHub Pages (free, ~10 min), Vercel (free, ~5 min), or your radhaarc.com domain | 1 hour | Play Console listing update in M5 |

**Default for privacy hosting:** GitHub Pages under `urmillive/dice-rollar-legal` repo — zero-cost, version-controlled, no extra account.

---

## 1. Verdict (read this first)

**Ship a hybrid: AdMob (banner + interstitial-on-cooldown) + a single one-time IAP "Remove Ads" at $1.99.** No subscriptions, no cosmetic packs in v1, no rewarded ads in v1.

Why this shape:
- Dice rollers are a low-intent utility category. Subscriptions and cosmetic packs underperform vs. an offensively-cheap one-time unlock.
- Banner alone earns pennies; interstitial alone annoys; the **combo** is the standard "free utility" pattern users tolerate.
- "Remove Ads" at $1.99 is the price point with the strongest conversion in the utility category and clears Play's de-minimis tax thresholds cleanly.

Expected revenue (modest, honest):
- 1k DAU × $0.05 banner eCPM-day + $0.20 interstitial eCPM-day ≈ **$7/month from ads**
- 1k DAU × 0.3% lifetime IAP conversion × $1.99 ≈ **$6/month from IAP** (steady state)
- **~$13/month at 1k DAU.** This is portfolio-tier income, not a business. Treat it as instrumentation practice + a real Play Console workflow.

---

## 2. What stays free vs. paid

| Feature | Free | Pro ($1.99 one-time) |
|---|---|---|
| All 7 die types (d4–d100) | ✅ | ✅ |
| Multi-die count (1–12) | ✅ | ✅ |
| Modifier (-99..+99) | ✅ | ✅ |
| Roll history (last 20) | ✅ | ✅ (extended to last 200) |
| Settings (dots/numbers, borders, colors) | ✅ | ✅ |
| Adaptive grid + haptics | ✅ | ✅ |
| **Banner ad on Roll screen** | ✅ | ❌ removed |
| **Interstitial every 10 rolls (60s cooldown)** | ✅ | ❌ removed |

Pro **only** removes ads and extends history. No paywalled core features — that path leads to 1-star reviews on a utility app.

---

## 3. Tech approach

### 3.1 Dependencies (add to `gradle/libs.versions.toml`)

```toml
play-services-ads = "23.6.0"      # AdMob SDK
user-messaging-platform = "3.0.0" # UMP consent (GDPR/CCPA)
billing-ktx = "7.1.1"             # Play Billing v7 + Kotlin coroutines wrapper
```

### 3.2 Manifest changes (`app/src/main/AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<application ...>
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX" />
</application>
```

App ID lives in `local.properties` (`ADMOB_APP_ID=…`) and is injected via `BuildConfig` / manifest placeholders — **never hardcoded**. Test ID `ca-app-pub-3940256099942544~3347511713` for debug builds.

**App size budget:** play-services-ads adds ~3–4 MB to the AAB (current release is ~3.0 MB). Expect the v1.1.0 AAB to land around **6–7 MB** — still tiny by Play standards but note for the listing's "App size" badge.

### 3.3 Module layout

```
app/src/main/java/com/radhaarc/dicerollar/
  monetization/
    AdsInitializer.kt              # MobileAds.initialize + consent gate
    ConsentManager.kt              # UMP form, returns Flow<ConsentState>
    BannerAdView.kt                # @Composable AndroidView wrapper around AdView
    InterstitialController.kt      # loads, caches, shows; respects cooldown + Pro flag
    AdCadence.kt                   # pure logic: shouldShowInterstitial(rollCount, lastShownAt)
  billing/
    BillingClientWrapper.kt        # connect, query SKU, launchPurchaseFlow
    PurchaseRepository.kt          # Flow<EntitlementState> backed by DataStore + verification
    Entitlement.kt                 # sealed class: Free | Pro
```

`PurchaseRepository` is the single source of truth for the "show ads" bit — every ad surface gates on `entitlement.collect { state -> if (state is Free) load() }`.

### 3.4 ViewModel + Compose glue

- `RollViewModel` takes a new constructor arg `entitlements: PurchaseRepository`. Adds `entitlement` to `RollUiState`.
- `RollScreen` reads `state.entitlement`; conditionally composes `BannerAdView` above the bottom bar.
- After each roll, `RollViewModel.onRollSettled()` calls `InterstitialController.maybeShow(rollCount)` — controller checks Pro flag + cooldown + load state.

**AdView lifecycle (mandatory):** `BannerAdView` uses `AndroidView` + `DisposableEffect` to call `adView.pause()` on lifecycle pause, `adView.resume()` on resume, and `adView.destroy()` on dispose. Skipping this leaks the Activity on every navigation. Sample:

```kotlin
DisposableEffect(adView) {
    val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE  -> adView.pause()
            Lifecycle.Event.ON_RESUME -> adView.resume()
            else -> Unit
        }
    }
    lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        adView.destroy()
    }
}
```

**MobileAds.initialize cold-start cost:** ~500 ms on mid-range devices. Do **not** call it in `Application.onCreate` — call it lazily from `AdsInitializer.initIfNeeded()` triggered after the first frame of the Roll screen (`LaunchedEffect(Unit) { ... }`). Splash budget stays clean.

### 3.5 IAP flow

1. User taps "Remove Ads" link in `SettingsScreen` (new row, above "Appearance").
2. Opens `PurchaseSheet` (bottom sheet) showing price from Play (queried, not hardcoded), bullet list of what unlocks, "Buy" button, "Restore Purchases" link.
3. Buy → `BillingClient.launchBillingFlow()`.
4. On `PurchasesUpdatedListener` success → `acknowledgePurchase()` → `PurchaseRepository.markPro()`.
5. State persists in DataStore (`pro_unlocked = true`). On every app launch we also re-`queryPurchases()` and reconcile — protects against the user reinstalling on the same Google account.

**Purchase state matrix (handle ALL of these, not just SUCCESS):**

| `Purchase.purchaseState` | Action |
|---|---|
| `PURCHASED` + `isAcknowledged == false` | `acknowledgePurchase()` then `markPro()` |
| `PURCHASED` + `isAcknowledged == true` | `markPro()` (idempotent) — happens on re-launch after a prior buy |
| `PENDING` | **Critical for India (UPI/Net Banking).** Show "Payment processing — Pro unlocks when settled." Persist nothing yet. Re-`queryPurchases()` on next launch picks up the eventual `PURCHASED`. |
| `UNSPECIFIED_STATE` | Treat as Free; log warning |

**Voided / refunded purchases:** Play's 48 h auto-refund flips a `PURCHASED` purchase to **revoked** silently — `queryPurchases()` no longer returns it. So `PurchaseRepository.reconcile()` must do `if (queryPurchases().isEmpty()) markFree()` on every launch — DataStore is a *cache*, never the source of truth. Skipping this = users keep Pro after refund.

**Family Library:** disabled in v1. (Play Console → Monetize → Products → `pro_remove_ads` → Family Library = OFF.) Revisit only if support requests pile up.

**Localized pricing:** Use Play's auto-conversion default. Cap not needed — Play won't let it drop below ~$0.99 equivalent in any market.

### 3.6 Consent (UMP, mandatory for EEA + Brazil)

- On first launch, `ConsentManager.requestConsent()` → if `consentStatus == REQUIRED` show the UMP form.
- Block ad SDK initialization until consent resolved.
- "Privacy" row in Settings opens `consentInformation.showPrivacyOptionsForm()` so users can change consent later (required by GDPR).

### 3.7 Test IDs (use these for the entire dev cycle — real IDs only in release builds)

| Format | Test Ad Unit ID |
|---|---|
| Banner | `ca-app-pub-3940256099942544/6300978111` |
| Interstitial | `ca-app-pub-3940256099942544/1033173712` |

Real IDs come from the AdMob console **after** the app is reviewed and approved in Play Console — chicken-and-egg means v1.1.0 must ship to internal-testing first with test IDs, then a v1.1.1 swap to real IDs once AdMob links the app.

### 3.8 ProGuard / R8 keep rules

Release builds (`minifyEnabled = true`) **will crash** on first Billing query or AdMob load without keep rules. The bundled rules from each SDK are usually enough, but verify by running the release variant on the test phone before tagging.

Add to `app/proguard-rules.pro`:

```proguard
# Play Billing — keep callback classes called via reflection
-keep class com.android.billingclient.api.** { *; }

# Play Services Ads — bundled rules handle most, but add for safety
-keep public class com.google.android.gms.ads.** { public *; }

# UMP consent
-keep class com.google.android.ump.** { *; }
```

**Test gate:** before merging M5, build `:app:bundleRelease`, install the resulting AAB via `bundletool build-apks --connected-device`, and complete one full IAP loop on the device. Debug builds passing means nothing for R8.

### 3.9 Offline & error handling

| Surface | Offline behavior |
|---|---|
| Banner | `AdView.loadAd()` fails silently → empty 50dp gap. Acceptable. |
| Interstitial | `loadAd` fails → controller stays in `NotLoaded` state, never shows. Acceptable. |
| First-launch UMP | No network → `consentInformation.requestConsentInfoUpdate()` returns an error → treat as "consent unknown" → don't init ads this session, retry on next launch. |
| Purchase flow | Detect `ConnectivityManager` no-network → disable Buy button + show "You're offline" inline. Don't even attempt `launchBillingFlow`. |
| Restore | Show "Offline — try again when connected" inline; don't fire `queryPurchases()`. |

---

## 4. UX rules (non-negotiable, keeps 1-star reviews down)

| Rule | Why |
|---|---|
| Banner sits **above the bottom bar**, never above the dice grid | Dice area is the product; ads must never crop dice |
| Interstitial: max 1 per 60s **and** every 10th roll | Cooldown beats frequency cap alone |
| **No interstitial on first 5 rolls of a new install** | First-impression bounces kill retention |
| **No ads on settings/history screens** | Reduces "ad-everywhere" rage; keeps it as roll-screen-only |
| Pro purchase removes ad surfaces immediately (no app restart) | Reactive Flow from `PurchaseRepository` handles this |
| Restore Purchases is one tap, always visible in the Pro sheet | Required by Play policy; failing this = rejection |
| Failed purchase shows an inline error, not a toast that vanishes | Conversion path must surface what went wrong |
| **Banner is ≥ 50dp from any tappable** (count steppers, gear, history icon) | AdMob policy — failing this risks ad account suspension, not just rejection |
| **Existing v1.0.2 users see the UMP consent on first launch post-update** | Plan accordingly: release notes should say "we've added an option to remove ads + a privacy/consent dialog" so it's not surprise behavior |

---

## 5. Play Console changes (do these in lockstep with the code PR)

1. **Data Safety form** — flip:
   - Ads: **Yes**
   - Collects: Advertising ID, IP address, app interactions, crash logs
   - Shared with third parties: **Yes** (Google AdMob)
2. **Content rating** — re-submit IARC; AdMob alone doesn't change rating, but interstitials trigger an "Ads" disclosure prompt.
3. **Privacy policy** — append AdMob + UMP disclosure section to `play-store/privacy-policy.md`. Re-host. Update the URL in the Play Console listing.
4. **In-app products** — create `pro_remove_ads` ($1.99, one-time, managed product) in Play Console → Monetize → Products.
5. **AdMob Console** — link the Play Store app, create the two ad units (banner + interstitial), copy real IDs into `local.properties` for the **release** keystore build only.

---

## 6. Implementation phases (each = its own PR off this branch)

Each phase has a **Definition of Done (DoD)** — concrete checks that must pass before the PR merges. "Scaffolded" is not done.

| Phase | PR title | Est. effort | Scope | DoD |
|---|---|---|---|---|
| M1 | `feat(billing): scaffold PurchaseRepository + Entitlement state` | 1 weekend | Billing client connect/disconnect, query products, query existing purchases, DataStore persistence | Unit tests on Entitlement reducer (`PURCHASED → Pro`, `PENDING → Free`, `revoked → Free`); manual: license-tester account completes test buy on internal track |
| M2 | `feat(monetization): Pro upgrade sheet + Settings entry point` | 1 weekend | "Remove Ads" settings row, bottom-sheet UI, Restore button, offline + error states | Manual: test buy from sheet, restore on fresh install, offline state shows correct disabled UI |
| M3 | `feat(ads): consent + AdsInitializer + banner on RollScreen (test IDs)` | 1 weekend | UMP consent flow, lazy MobileAds init, BannerAdView with DisposableEffect lifecycle | Banner loads on test IDs; Pro flag hides it within 1 frame; rotate device 10× → no leak (verify via `adb shell dumpsys meminfo`) |
| M4 | `feat(ads): interstitial controller with cadence gate` | 0.5 weekend | Pure `AdCadence` unit-tested first, then wired into roll loop | Unit tests for cadence (cooldown, first-5-rolls-skip, 10th-roll-trigger); manual: 12 rolls show exactly 1 interstitial |
| M5 | `docs+chore: privacy policy update + Data Safety prep + release-1.1.0 notes + R8 verify` | 0.5 weekend | Update `play-store/`, bump version, write release notes, run release-build IAP smoke test | `:app:bundleRelease` installs and completes one full IAP loop on the device |
| M6 | `chore(release): switch to real AdMob unit IDs` | 1 hour | After v1.1.0 is live in internal testing and AdMob has linked the app | Real banner + interstitial render with non-test labels; AdMob console shows impressions |

**Total estimate:** ~4 weekends of focused work + ~1 week of external lead time (banking, AdMob review).

**Release track ladder:** internal (license testers only, real billing) → closed (10–20 friends) → open (public beta, optional) → production. Every phase merges to `main` and ships to **internal** first. Production rollout only after M6 + a clean week on closed testing.

---

## 7. Risks & decisions to make before M1

| # | Decision needed | Default if not decided |
|---|---|---|
| D1 | Pro price: $1.99 vs $2.99 | **$1.99** — utility category benchmark |
| D2 | Show banner on History screen too? | **No** — keeps it as a "Pro perk" the user notices when they go there |
| D3 | Rewarded ads as a "watch ad to extend history" hook? | **No in v1** — adds SDK surface for unclear lift |
| D4 | UMP form OR build a hand-rolled consent screen? | **UMP** — official Google solution, no rejection risk |
| D5 | Subscription tier later (themes/sounds)? | **Defer** — revisit only if ad+IAP MRR > $50/mo |

---

## 8. What this plan explicitly does **not** do

- No analytics / Firebase. Adds privacy surface + Data Safety complexity for ~zero useful signal at this DAU.
- No A/B testing infra. Premature for a portfolio-tier project.
- No server. All entitlement state local; reconciled via `BillingClient.queryPurchases()` on launch.
- No multi-die-type-in-one-roll. That gap from the audit ships in **1.0.3** (separate branch) before 1.1.0. → see `MULTI-DIE-PLAN.md` (TBD).

---

## 9. Decision log

### 2026-06-27
- Folded gap-find feedback into the plan:
  - Added §0 pre-flight (merchant/tax/AdMob lead times, promo codes, privacy hosting decision = GitHub Pages).
  - §3.4 expanded with AdView `DisposableEffect` lifecycle + lazy MobileAds init.
  - §3.5 expanded with purchase-state matrix (incl. UPI `PENDING`), revoke handling, Family Library OFF.
  - New §3.8 ProGuard / R8 keep rules + release-build IAP smoke test as a gate.
  - New §3.9 offline & error handling per surface.
  - §4 added AdMob 50dp tap-target rule + first-launch-post-update UMP UX note.
  - §6 added Definition of Done per phase, effort estimates (~4 weekends), and the internal → closed → open → prod release-track ladder.
  - New §10 rollback / kill switch (BuildConfig + remote JSON override).

### 2026-06-24
- Plan drafted. Hybrid AdMob + one-time IAP. No subscriptions. UMP for consent. Test IDs throughout dev; real IDs only for release builds. Defaults locked unless overridden in §7.

---

## 10. Rollback / kill switch

Ads tanking retention shouldn't require a 4-hour rebuild + Play rollout. Two-layer kill switch:

### Layer 1 — Compile-time (always present)

`BuildConfig.ADS_ENABLED: Boolean` — defaults `true`. Setting it to `false` and shipping a patch disables all ad surfaces immediately. Both `BannerAdView` and `InterstitialController` short-circuit on this flag.

```kotlin
// app/build.gradle.kts
buildConfigField("boolean", "ADS_ENABLED", "true")
```

### Layer 2 — Runtime override (fetched at launch)

A 1-line JSON hosted at `https://urmillive.github.io/dice-rollar-legal/config.json`:

```json
{ "ads_enabled": true, "interstitial_cadence": 10, "interstitial_cooldown_seconds": 60 }
```

`KillSwitchFetcher` fetches with a 3 s timeout on app launch; on failure or stale (>24 h) cache, uses BuildConfig defaults. Lets you turn ads off **without** shipping a new APK — useful for "AdMob suspended my account at 2 AM" or "interstitials are causing 30% bounce."

**No Firebase Remote Config** — the static JSON is enough for kill switches and avoids the Firebase SDK bulk + Data Safety implications.

### Triage thresholds (when to flip the switch)

| Signal | Threshold | Action |
|---|---|---|
| Play Console crash-free users | < 99% over 24h | Flip `ads_enabled = false`, investigate |
| Day-1 retention | drop > 10pp vs. v1.0.2 baseline | Flip interstitials off (set cadence to 9999), keep banner |
| Review average | drops below 4.0 over 7 days | Read every new 1–2 star review, react |
| AdMob account warning email | any | Stop the bleed immediately — flip ads off via Layer 2 |
