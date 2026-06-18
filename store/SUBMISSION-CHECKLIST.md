# Dice Rollar — Play Console Submission Checklist

Pre-computed answers for the Play Console setup forms. Open Play Console → Create app → walk these in order.

## Create app

| Field | Answer |
|---|---|
| App name | Dice Rollar |
| Default language | English – United States (en-US) |
| App or game | App |
| Free or paid | Free |
| Declarations | All boxes checked (user programs / Play guidelines) |

## App content (left rail)

### Privacy policy
- URL: `https://urmillive.github.io/dicerollar-privacy/` — **published, ready to paste**

### App access
- All functionality available without restrictions: **Yes**

### Ads
- Contains ads: **No**

### Content rating (IARC questionnaire)
- Category: **Reference, News, or Educational**
- Violence, sexuality, language, drugs, gambling, etc.: **All No**
- Does the app collect / share personal information: **No**
- Expected rating: **Everyone**

### Target audience
- Target age groups: **18 and over** (simplest path; avoids COPPA / DAC requirements). Alternatively, all ages — but that triggers Designed for Families review.
- Children's privacy policy: N/A (since not targeted at children)

### News app: **No**
### COVID-19 contact tracing or status app: **No**
### Data safety
- Does your app collect or share any of the required user data types: **No**
- All categories (location, personal info, financial info, etc.): **Not collected**
- Justification: app is 100% local, no network, no analytics. Confirm: only INTERNET permission is from default RN/AndroidX inclusion — runtime makes zero outbound calls.
- Encrypted in transit: N/A
- User can request data deletion: N/A
- Independent security review: No

### Government apps: **No**
### Financial features: **No**
### Health: **No**

## Main store listing (left rail)

| Field | Source |
|---|---|
| App name | `store/listing.md` → App name |
| Short description | `store/listing.md` → Short description (68/80 chars) |
| Full description | `store/listing.md` → Full description |
| App icon | `store/icon-512.png` |
| Feature graphic | `store/feature-graphic-1024x500.png` |
| Phone screenshots | `store/screenshots/` (capture 4-6 per `store/SCREENSHOTS.md`) |
| 7-inch tablet screenshots | Optional, skip for now |
| 10-inch tablet screenshots | Optional, skip for now |
| App category | Primary: **Tools**. Tags: dice, roller, tabletop, rpg, d20 |
| Contact details — email | urmillive@gmail.com |
| Contact details — phone | Optional, skip |
| Contact details — website | Optional, skip |

## Production track / Release dashboard

1. **Internal testing first** (recommended) → Production. Even for solo apps, internal testing gives you a 1-hour turnaround instead of multi-day review on each upload.
2. **App bundle**: upload `app/build/outputs/bundle/release/app-release.aab` (3 MB, signed)
3. **Release name**: `1.0.0` (auto-filled from versionName)
4. **Release notes** (en-US): `First release. 7 dice types, multi-roll, history, dark theme.`

## App signing (one-time)

- Use Play App Signing: **Yes** (recommended — Google manages the production signing key, your upload keystore at `app/upload-keystore.jks` becomes the upload key)
- Upload your existing keystore as the upload key: yes (the AAB is already signed with it)

## Pre-launch report
- Auto-enabled when you upload to internal testing track. Wait for the report (~1-2h) — it'll catch crashes on real devices.

## Submit for review
After all sections show green:
1. Internal testing track: instant publish (no review)
2. Production track: 1-7 day review

---

**Bottom line**: this app has no data collection, no ads, no auth, no third-party SDKs. The forms are mostly "no" answers. Expected total form time: 15 min.
