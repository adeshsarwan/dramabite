# DramaMunch — AAP008 - ShortDrama 13

Clone of DramaBite with the same content. Branch: `app/AAP008-dramamunch`.

| | |
|---|---|
| App name | **DramaMunch** |
| Package (applicationId) | `com.dramamunch.thebes.app` |
| Internal code namespace | `com.dramabite.thebes.app` (unchanged, not visible to users) |
| Version | versionCode 1 / 1.0.0 → Remote Config key `appVersionCode1` |
| Accent colour | `#F05A1A` |

## Tutorial SDK: kite (AAP008 only)
AAP008 runs The Arrows' **kite** SDK (`com.tutorial:tutorial-kite:1.0.0-kite-alpha08` + `com.arrows.ads:ads:1.2.0-alpha05`), not the classic `com.tutorial:tutorial` that DramaBite / AAP009 / AAP010 use.
- Flow: kite Splash (launcher) → Language → Intro (4 pages) → Age check → Home; Welcome Back on return.
- Setup: `tutorial/TutorialBootstrap.kt` (`KiteTutorial.init`), host Splash work in `tutorial/SdkSplashActivity.kt`, units in `tutorial/DramaBiteAdUnits.kt` — all 19 ad-script placements incl. `…Repeat`, `305_onb_a_native` (intro end card) and `306_age_n_native`.
- Firebase Remote Config: publish the `tut_*` keys from `kite_remote_config_AAP008.json` (real AAP008 units, brand colours). Do **not** paste The Arrows' sample JSON — its `unit` values are Google test IDs and would override the real ones.
- The picked language is copied from the app locale into the content-API language on Home (`syncLanguageFromAppLocale`). Share sheets and Google sign-in call `skipWelcomeBackOnce()` so returning does not show Welcome Back.
- Home ads stay host-owned: collapsible native `401_home_a_native_col`, banner + inter via Remote Config `publisher_Banner_Ad_Id` / `publisher_Interstitial_Ad_Id`.

## Done (from AAP008 Adscript)
AdMob App ID, all ad-script units, Remote Config ad units (`test_id: false`), Facebook ID + client token, Adjust token + revenue / purchase events.

## Facebook key hash (upload key)
- Hash key: `AbxBR395U61P8167kvrcw1RmXpI=`
- SHA-1: `01:BC:41:47:7F:79:53:AD:4F:F3:5E:BB:92:FA:DC:C3:54:66:5E:92`
- After the first upload with Play App Signing, also add the **app signing key** hash from Play Console → App integrity (convert its SHA-1 to base64) in Meta.

> `time_show_inter_home` = 10 / `show_401_home_a_inter` = true are in the host fallback JSON but the app's own home-interstitial code does not read them yet.

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
