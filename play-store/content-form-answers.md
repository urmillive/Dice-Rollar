# Play Console — Pre-filled Form Answers (v1.0.1 internal)

Copy these answers into each section of the Play Console. All are based on the current v1.0.1 build (no ads, no network, no data collection, no in-app purchases).

---

## App access
**Answer:** *All functionality is available without any restrictions.*
No login, no region-locked features, no premium tier.

## Ads
**Answer:** *No, my app does not contain ads.*
NOTE: This will change to "Yes" in Phase 2 when AdMob is wired in. Remember to update.

## Content rating (IARC questionnaire)
Category: **Utility, Productivity, Communication, or Other**

| Question | Answer |
|---|---|
| Violence | No |
| Sexuality | No |
| Language | No |
| Controlled substances | No |
| Crude humour | No |
| Gambling — does the app simulate or enable real-world gambling? | **No** (polyhedral dice for tabletop RPGs are not classified as gambling by IARC) |
| Simulated gambling | No |
| User-generated content | No |
| Users can interact / share location | No |
| Digital purchases | No |
| Unrestricted internet access | No |
| Personal information shared | No |

Expected rating: **Everyone / 3+**.

## Target audience and content
- **Target age group:** 13 and over (safest for a non-children's utility; avoids Designed-for-Families program overhead)
- **Appeals to children:** No
- **Adult themes:** No

## News app
**No.**

## COVID-19 contact tracing app
**No.**

## Government app
**No.**

## Financial features
**No.**

## Health
**No.**

## Data safety
- **Does your app collect or share any of the required user data types?** **No**
- **Is all of the user data collected by your app encrypted in transit?** N/A (no data collected)
- **Do you provide a way for users to request that their data be deleted?** N/A (no data collected; uninstall removes the on-device roll history)

NOTE: When AdMob is added in Phase 2, this becomes Yes for "Device or other identifiers" — AdMob uses Advertising ID. Update accordingly.

## Privacy policy URL
**Required.** Host `play-store/privacy-policy.md` somewhere reachable (suggestions below) and paste the URL.

Quick hosting options:
1. **GitHub Pages** — push the policy to a public repo, enable Pages, point to `https://<user>.github.io/<repo>/privacy`
2. **Vercel / Netlify** — static site, free, instant
3. **radhaarc.com/dice-rollar/privacy** — if you control the domain

## Internal testing — testers list
- Add your own Google account
- Add 1–2 friends' Gmail addresses
- OR create a Google Group and add the group's email here (easier to manage long-term)

## Release notes (internal)
See `play-store/release-notes-internal.txt`.

## Store listing copy
- Short description: see `play-store/short-description.txt` (76/80 chars)
- Full description: see `play-store/full-description.txt` (well under 4000)

## Store listing assets (NOT needed for internal testing, needed for Production)
- App icon: 512 × 512 PNG (currently a placeholder d20 vector — needs design pass before Production)
- Feature graphic: 1024 × 500 PNG
- Phone screenshots: minimum 2, max 8, 16:9 or 9:16
- Optional: 7-inch tablet screenshots, 10-inch tablet screenshots

## Countries / regions
- For internal testing: doesn't matter (testers are explicit)
- For Production: start with **all countries** unless you have a specific reason to restrict

## Pricing
- **Free**
- Set in Play Console → Monetization setup → Pricing
