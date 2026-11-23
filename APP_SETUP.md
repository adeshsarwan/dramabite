# DramaNibble — AAP009 - ShortDrama 14

Clone of DramaBite with the same content. Branch: `app/AAP009-dramanibble`.

| | |
|---|---|
| App name | **DramaNibble** |
| Package (applicationId) | `com.dramanibble.thebes.app` |
| Internal code namespace | `com.dramabite.thebes.app` (unchanged, not visible to users) |
| Version | versionCode 1 / 1.0.0 → Remote Config key `appVersionCode1` |
| Accent colour | `#0E9F92` |

## Still TODO before release (values not yet in the checklist)

Search the code for `TODO(AAP009)` and `TODO_`.

| Checklist row | What | Where |
|---|---|---|
| 4 / 10 / Ads script | AdMob App ID | `app/src/main/AndroidManifest.xml` (Google test ID for now) |
| 10 / Ads script | Ads script unit IDs | `app/src/main/java/.../tutorial/DramaBiteAdUnits.kt` (Google test IDs for now) |
| 10 / Key remote | In-app ad units + `test_id: false` | Firebase Remote Config param `appVersionCode1` (fallback: `app/src/main/assets/remote_config.json`, currently `test_id: true`) |
| 13 / 14 | Facebook App ID + client token | `app/build.gradle` (`FACEBOOK_APP_ID`), `res/values/strings.xml` |
| 15 / 16 | Adjust token + ad revenue / purchase event tokens | `app/build.gradle` |
| 26 | Real `google-services.json` for `com.dramanibble.thebes.app` | `app/google-services.json` (placeholder now — login, Firestore, Remote Config fetch will not work until replaced) |
| 24 / 25 / 35 | Privacy Policy + Terms URLs for this app | Remote Config `url_privacy_policy`, `url_help_feedback` (still DramaBite's) |
| 32 | app-ads.txt | Upload `app-ads.txt` (repo root, includes Bigo) to the developer website listed on Play |
| 1 | Bigo Ads + Pangle KR mediation | Confirm with The Arrows that `com.arrows.ads:ads` version bundles these adapters |
| 47 | Upload keystore | Create a NEW keystore for this app; keep it + `keystore.properties` out of git (see `keystore.properties.example`) |

## Build
`local.properties` needs `gpr.user` / `gpr.token` (GitHub Packages for The Arrows SDKs). Then `./gradlew bundleRelease`.
