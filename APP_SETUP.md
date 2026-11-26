# DramaNibble — AAP009 - ShortDrama 14

Clone of DramaBite with the same content. Branch: `app/AAP009-dramanibble`.

| | |
|---|---|
| App name | **DramaNibble** |
| Package (applicationId) | `com.dramanibble.thebes.app` |
| Internal code namespace | `com.dramabite.thebes.app` (unchanged, not visible to users) |
| Version | versionCode 1 / 1.0.0 → Remote Config key `appVersionCode1` |
| Accent colour | `#0E9F92` |

## Done (from AAP009 Adscript)
AdMob App ID, all ad-script units, Remote Config ad units (`test_id: false`), Facebook ID + client token, Adjust token + revenue / purchase events.

## Facebook key hash (upload key)
- Hash key: `gWpKv0z0L1RnWvSGLt9U7vCPLOk=`
- SHA-1: `81:6A:4A:BF:4C:F4:2F:54:67:5A:F4:86:2E:DF:54:EE:F0:8F:2C:E9`
- After the first upload with Play App Signing, also add the **app signing key** hash from Play Console → App integrity (convert its SHA-1 to base64) in Meta.

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
