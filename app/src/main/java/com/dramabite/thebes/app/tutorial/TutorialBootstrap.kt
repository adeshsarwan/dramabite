package com.dramabite.thebes.app.tutorial

import android.app.Application
import android.util.Log
import com.ads.config.AdConfig
import com.ads.config.AdjustConfig
import com.ads.core.AdSdk
import com.dramabite.thebes.app.BuildConfig
import com.dramabite.thebes.app.R
import com.dramabite.thebes.app.activity.full_reels.ReelsShowActivity
import com.dramabite.thebes.app.activity.login.LoginMainActivity
import com.dramabite.thebes.app.activity.login.OtpReceiveScreen
import com.dramabite.thebes.app.activity.main.activity.HomeScreenActivity
import com.dramabite.thebes.app.activity.payment.CheckoutActivity
import com.dramabite.thebes.app.activity.payment.PaymentIntegrationScreen
import com.dramabite.thebes.app.activity.premium.PremiumActivity
import com.dramabite.thebes.app.activity.premium.PremiumMemberActivity
import com.dramabite.thebes.app.activity.premium.TopUpActivity
import androidx.compose.ui.graphics.Color
import com.tutorial.TutorialSdk
import com.tutorial.config.OnboardingImageScale

/**
 * AdSdk then TutorialSdk — Splash → Language → SDK Onboarding (branded) →
 * InterOnboarding → Home. Ads script IDs in [DramaBiteAdUnits].
 */
object TutorialBootstrap {

    private const val TAG = "TutorialBootstrap"

    fun init(app: Application) {
        initAdsSdk(app)

        TutorialSdk.onEvent = { event, params ->
            Log.d(TAG, "event=$event params=$params")
        }

        TutorialSdk.init(app) {
            homeActivity = HomeScreenActivity::class

            welcomeExcludedActivities += ReelsShowActivity::class
            welcomeExcludedActivities += CheckoutActivity::class
            welcomeExcludedActivities += PaymentIntegrationScreen::class
            welcomeExcludedActivities += PremiumActivity::class
            welcomeExcludedActivities += PremiumMemberActivity::class
            welcomeExcludedActivities += TopUpActivity::class
            welcomeExcludedActivities += LoginMainActivity::class
            welcomeExcludedActivities += OtpReceiveScreen::class

            ads {
                consent = true
                // LAA006 ads script — placement DSL
                ad101SplNNative = DramaBiteAdUnits.AD_101_SPL_N_NATIVE
                ad101SplONative = DramaBiteAdUnits.AD_101_SPL_O_NATIVE
                ad102SplNInter = DramaBiteAdUnits.AD_102_SPL_N_INTER
                ad102SplOInter = DramaBiteAdUnits.AD_102_SPL_O_INTER
                ad103EndcardNNative = DramaBiteAdUnits.AD_103_ENDCARD_N_NATIVE
                ad103EndcardONative = DramaBiteAdUnits.AD_103_ENDCARD_O_NATIVE
                ad201Lfo1NNative = DramaBiteAdUnits.AD_201_LFO1_N_NATIVE
                ad202Lfo2NNative = DramaBiteAdUnits.AD_202_LFO2_N_NATIVE
                ad203Lfo3NNative = DramaBiteAdUnits.AD_203_LFO3_N_NATIVE
                ad301Onb1NNative = DramaBiteAdUnits.AD_301_ONB1_N_NATIVE
                ad303Onb3NNativeFull = DramaBiteAdUnits.AD_303_ONB3_N_NATIVE_FULL
                ad304Onb4NNative = DramaBiteAdUnits.AD_304_ONB4_N_NATIVE
                ad305OnbNInter = DramaBiteAdUnits.AD_305_ONB_N_INTER
                ad401WcbONative = DramaBiteAdUnits.AD_401_WCB_O_NATIVE
                ad401WcbOInter = DramaBiteAdUnits.AD_401_WCB_O_INTER
            }

            theme {
                val brand = hex("#0E9F92")
                primary = brand
                onPrimary = hex("#FFFFFF")
                onboardingButtonBackground = brand
                nativeAdCta = brand
                nativeAdOnCta = Color.White
                nativeAdBadge = brand
                nativeAdOnBadge = Color.White
                nativeAdBackground = hex("#10083A")
                nativeAdText = Color.White
                endCardCta = brand
                endCardOnCta = Color.White
                onboardingBackground = hex("#10083A")
                splashBackground = hex("#10083A")
                splashText = Color.White
                // Welcome back — match native ad dark backdrop
                welcomeBackground = hex("#10083A")
                welcomeText = Color.White
                welcomeTextSecondary = hex("#C9C2D6")
                languageBackground = hex("#16102C")
                languageText = Color.White
                languageTextSecondary = hex("#C9C2D6")
                languageRowBackground = hex("#241C3D")
                pageIndicatorActive = brand
                pageIndicatorInactive = Color.White
            }

            screens {
                splash = SdkSplashActivity::class
                language = SdkLanguageActivity::class
                onboarding = SdkOnboardingActivity::class
            }

            // Same group + country variants as FirstOpenTutorial-Sample (flags, not default text)
            languages {
                group("fr", R.string.french, R.string.lang_french_native) {
                    variant("fr-FR", R.string.french, R.drawable.flag_fr)
                    variant("fr-MC", R.string.country_monaco, R.drawable.flag_mc)
                    variant("fr-CD", R.string.country_congo, R.drawable.flag_cd)
                }
                group("en", R.string.english) {
                    variant("en-US", R.string.country_american, R.drawable.flag_us)
                    variant("en-GB", R.string.country_british, R.drawable.flag_gb)
                    variant("en-PH", R.string.country_philippines, R.drawable.flag_ph)
                }
                group("hi", R.string.hindi, R.string.lang_hindi_native) {
                    variant("hi-IN", R.string.country_india, R.drawable.flag_in)
                    variant("hi-FJ", R.string.country_fiji, R.drawable.flag_fj)
                }
                group("es", R.string.spanish, R.string.lang_spanish_native) {
                    variant("es-ES", R.string.spanish, R.drawable.flag_es)
                    variant("es-MX", R.string.country_mexico, R.drawable.flag_mx)
                }
                group("pt", R.string.portuguese, R.string.lang_portuguese_native) {
                    variant("pt-PT", R.string.portuguese, R.drawable.flag_pt)
                    variant("pt-EU", R.string.country_eu, R.drawable.flag_eu)
                    variant("pt-BR", R.string.country_brazil, R.drawable.flag_br)
                }
                group("ru", R.string.russian, R.string.lang_russian_native) {
                    variant("ru-RU", R.string.country_russia, R.drawable.flag_ru)
                    variant("ru-UA", R.string.country_ukraine, R.drawable.flag_ua)
                    variant("ru-BY", R.string.country_belarus, R.drawable.flag_by)
                }
                group("de", R.string.german, R.string.lang_german_native) {
                    variant("de-DE", R.string.german, R.drawable.flag_de)
                    variant("de-AT", R.string.country_austria, R.drawable.flag_at)
                    variant("de-LU", R.string.country_luxembourg, R.drawable.flag_lu)
                }
                group("ko", R.string.korean, R.string.lang_korean_native) {
                    variant("ko-KR", R.string.country_south_korea, R.drawable.flag_kr)
                    variant("ko-KP", R.string.country_north_korea, R.drawable.flag_kp)
                }
                language("ja", R.string.japanese, R.drawable.flag_jp, R.string.lang_japanese_native)
                language("it", R.string.italian, R.drawable.flag_it, R.string.lang_italian_native)
                language("zh", R.string.chinese, R.drawable.flag_cn, R.string.lang_chinese_native)
            }
            // FIT_HEIGHT keeps the top logo on short screens. CENTER_CROP clips it.
            onboarding {
                page(
                    R.drawable.tutorial_onboarding_page_1,
                    R.string.tutorial_onb_title_1,
                    R.string.tutorial_onb_desc_1,
                    OnboardingImageScale.FIT_HEIGHT,
                )
                page(
                    R.drawable.tutorial_onboarding_page_2,
                    R.string.tutorial_onb_title_2,
                    R.string.tutorial_onb_desc_2,
                    OnboardingImageScale.FIT_HEIGHT,
                )
                page(
                    R.drawable.tutorial_onboarding_page_3,
                    R.string.tutorial_onb_title_3,
                    R.string.tutorial_onb_desc_3,
                    OnboardingImageScale.CENTER_CROP,
                )
                fullNativeDefault(
                    R.drawable.tutorial_onboarding_page_3,
                    R.string.tutorial_onb_title_3,
                    R.string.tutorial_onb_desc_3,
                    OnboardingImageScale.CENTER_CROP,
                )
                page(
                    R.drawable.tutorial_onboarding_page_4,
                    R.string.tutorial_onb_title_4,
                    R.string.tutorial_onb_desc_4,
                    OnboardingImageScale.FIT_HEIGHT,
                )
            }
        }
    }

    private fun initAdsSdk(app: Application) {
        val adConfig = AdConfig(application = app).apply {
            // Do not set `environment` here — Ads SDK derives sandbox/production from build type.
            adjustConfig = AdjustConfig(adjustToken = BuildConfig.ADJUST_TOKEN).apply {
                facebookAppId = BuildConfig.FACEBOOK_APP_ID
                adImpressionEventToken = BuildConfig.ADJUST_EVENT_AD_REVENUE
                purchaseEventToken = BuildConfig.ADJUST_EVENT_PURCHASE
            }
        }
        AdSdk.getInstance().init(context = app, adConfig)
    }
}
