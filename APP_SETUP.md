# DramaMunch — AAP008 - ShortDrama 13

Clone of DramaBite with the same content. Branch: `app/AAP008-dramamunch`.

| | |
|---|---|
| App name | **DramaMunch** |
| Package (applicationId) | `com.dramamunch.thebes.app` |
| Internal code namespace | `com.dramabite.thebes.app` (unchanged, not visible to users) |
| Version | versionCode 1 / 1.0.0 → Remote Config key `appVersionCode1` |
| Accent colour | `#F05A1A` |

## Done (from AAP008 Adscript)
AdMob App ID, all ad-script units, Remote Config ad units (`test_id: false`), Facebook ID + client token, Adjust token + revenue / purchase events.

## Facebook key hash (upload key)
- Hash key: `AbxBR395U61P8167kvrcw1RmXpI=`
- SHA-1: `01:BC:41:47:7F:79:53:AD:4F:F3:5E:BB:92:FA:DC:C3:54:66:5E:92`
- After the first upload with Play App Signing, also add the **app signing key** hash from Play Console → App integrity (convert its SHA-1 to base64) in Meta.

> AAP008's ad script has placements the current Tutorial SDK setup has no slot for: LFO1/2/3 + ONB1/ONB4 returning-user (`_o_`), `305_onb_a_native`, `306_age_n_native` (age screen). Their IDs are kept as constants in `DramaBiteAdUnits.kt` — wire them once The Arrows confirms the SDK property names. Home banner + home inter go through Remote Config (`time_show_inter_home` = 10, `show_401_home_a_inter` = true are in the fallback JSON but not read by the app code yet).

## Still TODO before release

| Checklist row | What | Where |
|---|---|---|
| 26 | Real `google-services.json` for this package | `app/google-services.json` (placeholder — login, Firestore, Remote Config fetch won't work until replaced) |
| 24 / 25 / 35 | Privacy Policy + Terms URLs for this app | Remote Config `url_privacy_policy`, `url_help_feedback` (still DramaBite's) |
| 32 | app-ads.txt | Upload `app-ads.txt` (repo root, includes Bigo) to the developer website on Play |
| 1 | Bigo Ads + Pangle KR mediation | Confirm the `com.arrows.ads:ads` version bundles these adapters |
| — | Remote Config in Firebase | Publish `remote_config.json` as parameter `appVersionCode1` |
| 47 | Upload keystore | Copy the delivered `.jks` to `jks/` and the matching `keystore.properties` to the repo root (both git-ignored) |

## Build
`local.properties` needs `gpr.user` / `gpr.token` (GitHub Packages for The Arrows SDKs). Then `./gradlew bundleRelease`.
