package com.dramabite.thebes.app.tutorial

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.ads.config.AdConfig
import com.ads.config.AdjustConfig
import com.ads.core.AdSdk
import com.dramabite.thebes.app.BuildConfig
import com.dramabite.thebes.app.R
import com.dramabite.thebes.app.activity.main.activity.HomeScreenActivity
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.tutorial.kite.KiteImageScale
import com.tutorial.kite.KiteTutorial

/**
 * AdSdk → kite tutorial SDK (com.tutorial:tutorial-kite).
 * Flow: Splash → Language → Intro (4 pages) → Age check → Home; Welcome Back on return.
 * The kite Splash is the app's launcher; [SdkSplashActivity] adds host work on it.
 * Ad units: [DramaBiteAdUnits] (AAP008 ad script).
 */
object TutorialBootstrap {

    private const val TAG = "TutorialBootstrap"
    private const val BRAND = 0xFFF05A1A.toInt()
    private const val BACKDROP = 0xFF10083A.toInt()
    private const val SURFACE = 0xFF241C3D.toInt()

    fun init(app: Application) {
        initAdsSdk(app)
        initKite(app)
    }

    private fun initAdsSdk(app: Application) {
        val adConfig = AdConfig(app).apply {
            environment = BuildConfig.AD_ENVIRONMENT
            // Meta app id / client token come from the manifest (facebook_app_id / facebook_client_token).
            adjustConfig = AdjustConfig(BuildConfig.ADJUST_TOKEN).apply {
                adImpressionEventToken = BuildConfig.ADJUST_EVENT_AD_REVENUE
                purchaseEventToken = BuildConfig.ADJUST_EVENT_PURCHASE
            }
        }
        AdSdk.getInstance().init(app, adConfig)
    }

    private fun initKite(app: Application) {
        KiteTutorial.init(app) {
            homeActivity = HomeScreenActivity::class
            ageCheck = true
            splashActivity = SdkSplashActivity::class

            branding {
                // No logo: Splash / Welcome Back show the launcher icon.
                appName = R.string.app_name
                tagline = R.string.app_tagline
                splashBackground = R.drawable.tutorial_onboarding_page_1
                welcomeBackImage = R.drawable.tutorial_onboarding_page_2
                welcomeBackImageScale = KiteImageScale.CENTER_CROP
            }

            // Kite catalogue: en, es, pt, fr, hi, de, ja, ko, ru. Order = display order unless
            // Remote Config tut_lang_ui.sort sets one.
            languages(listOf("en", "hi", "es", "pt", "fr", "de", "ru", "ko", "ja"))

            colors {
                background = BACKDROP
                surface = SURFACE
                accent = BRAND
                onAccent = 0xFFFFFFFF.toInt()
                text = 0xFFFFFFFF.toInt()
                textSecondary = 0xFFC9C2D6.toInt()
                outline = 0xFF5E5675.toInt()
                tagline = BRAND
                adBackground = 0xFF0B0628.toInt()
                adCta = BRAND
                adCtaText = 0xFFFFFFFF.toInt()
                adHeadline = BRAND
                adBody = 0xFFF2F2F2.toInt()
                adBadge = BRAND
                adBadgeText = 0xFFFFFFFF.toInt()
            }

            adUnits {
                splashNative = DramaBiteAdUnits.SPLASH_NATIVE
                splashInter = DramaBiteAdUnits.SPLASH_INTER
                splashEndCard = DramaBiteAdUnits.SPLASH_END_CARD
                languageLfo1 = DramaBiteAdUnits.LANGUAGE_LFO1
                languageLfo1Repeat = DramaBiteAdUnits.LANGUAGE_LFO1_REPEAT
                languageLfo2 = DramaBiteAdUnits.LANGUAGE_LFO2
                languageLfo2Repeat = DramaBiteAdUnits.LANGUAGE_LFO2_REPEAT
                languageLfo3 = DramaBiteAdUnits.LANGUAGE_LFO3
                languageLfo3Repeat = DramaBiteAdUnits.LANGUAGE_LFO3_REPEAT
                introOnb1 = DramaBiteAdUnits.INTRO_ONB1
                introOnb1Repeat = DramaBiteAdUnits.INTRO_ONB1_REPEAT
                introOnb4 = DramaBiteAdUnits.INTRO_ONB4
                introOnb4Repeat = DramaBiteAdUnits.INTRO_ONB4_REPEAT
                introFull = DramaBiteAdUnits.INTRO_FULL
                introInter = DramaBiteAdUnits.INTRO_INTER
                introEndCard = DramaBiteAdUnits.INTRO_END_CARD
                ageNative = DramaBiteAdUnits.AGE_NATIVE
                welcomeBackNative = DramaBiteAdUnits.WELCOME_BACK_NATIVE
                welcomeBackInter = DramaBiteAdUnits.WELCOME_BACK_INTER
            }

            // Exactly four pages.
            intro {
                page(R.drawable.tutorial_onboarding_page_1, R.string.tutorial_onb_title_1, R.string.tutorial_onb_desc_1)
                page(R.drawable.tutorial_onboarding_page_2, R.string.tutorial_onb_title_2, R.string.tutorial_onb_desc_2)
                page(R.drawable.tutorial_onboarding_page_3, R.string.tutorial_onb_title_3, R.string.tutorial_onb_desc_3)
                page(R.drawable.tutorial_onboarding_page_4, R.string.tutorial_onb_title_4, R.string.tutorial_onb_desc_4)
            }
        }
    }

    /**
     * Kite applies the picked language to the AppCompat locale but has no callback.
     * Copy it into the host prefs (content API language) when Home opens.
     */
    @JvmStatic
    fun syncLanguageFromAppLocale() {
        val tag = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: return
        if (tag.isBlank()) return
        if (ControlPreference.getLanguage() != tag || ControlPreference.getAppLanguage() != tag) {
            ControlPreference.setLanguage(tag)
            ControlPreference.setAppLanguage(tag)
            Log.i(TAG, "language synced from kite locale: $tag")
        }
        ControlPreference.setLanguageScreen(true)
    }

    /** Call right before sending the user to another app (share sheet, Google sign-in, browser). */
    @JvmStatic
    fun skipWelcomeBackOnce() {
        if (KiteTutorial.isInitialized) KiteTutorial.welcomeBack.skipNext()
    }
}
