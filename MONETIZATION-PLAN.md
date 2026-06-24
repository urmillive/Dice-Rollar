# Dice-Rollar — Monetization Plan

**Date:** 2026-06-24
**Branch:** `feat/monetization-plan`
**Target version:** 1.1.0 / versionCode 10 (skip 4–9 to leave room for hotfixes)
**Status:** ⬜ PLAN ONLY — no code in this branch yet.

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

### 3.5 IAP flow

1. User taps "Remove Ads" link in `SettingsScreen` (new row, above "Appearance").
2. Opens `PurchaseSheet` (bottom sheet) showing price from Play (queried, not hardcoded), bullet list of what unlocks, "Buy" button, "Restore Purchases" link.
3. Buy → `BillingClient.launchBillingFlow()`.
4. On `PurchasesUpdatedListener` success → `acknowledgePurchase()` → `PurchaseRepository.markPro()`.
5. State persists in DataStore (`pro_unlocked = true`). On every app launch we also re-`queryPurchases()` and reconcile — protects against the user reinstalling on the same Google account.

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

| Phase | PR title | Scope |
|---|---|---|
| M1 | `feat(billing): scaffold PurchaseRepository + Entitlement state` | Billing client, query, restore, DataStore persistence. No UI. Unit tests on `Entitlement` reducer. |
| M2 | `feat(monetization): Pro upgrade sheet + Settings entry point` | Settings row, bottom sheet, restore button. Mocks `BillingClientWrapper` in tests. |
| M3 | `feat(ads): consent + AdsInitializer + banner on RollScreen (test IDs)` | UMP gate, MobileAds init, BannerAdView composable. Pro flag hides banner. |
| M4 | `feat(ads): interstitial controller with cadence gate` | Pure `AdCadence` rules + tests, then wire into `RollViewModel.onRollSettled`. |
| M5 | `docs+chore: privacy policy update + Data Safety prep + release-1.1.0 notes` | Update `play-store/` content, bump `versionCode`/`versionName`, write release notes. |
| M6 | `chore(release): switch to real AdMob unit IDs` | After v1.1.0 is live in internal testing and AdMob has linked the app. |

M1–M5 ship as one merge into `main` as **v1.1.0 internal testing build**. M6 is a follow-up patch.

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
- No multi-die-type-in-one-roll. That gap from the audit ships in **1.0.3** (separate branch) before 1.1.0.

---

## 9. Decision log

### 2026-06-24
- Plan drafted. Hybrid AdMob + one-time IAP. No subscriptions. UMP for consent. Test IDs throughout dev; real IDs only for release builds. Defaults locked unless overridden in §7.
