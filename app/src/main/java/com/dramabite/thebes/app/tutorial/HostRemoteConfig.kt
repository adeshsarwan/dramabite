package com.dramabite.thebes.app.tutorial

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.dramabite.thebes.app.ads.ManegeUtilsView
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.dramabite.thebes.app.engineBox.client.ApiConfig
import com.dramabite.thebes.app.engineBox.client.EngineClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Host Firebase Remote Config for API URLs + host-page ad unit IDs.
 * SDK Splash skips [com.dramabite.thebes.app.activity.main.base.BaseActivity], so this
 * must run in [SdkSplashActivity.onSplashWork] or LanguagePreference / onboarding ads stay empty.
 */
object HostRemoteConfig {

    private const val TAG = "HostRemoteConfig"
    private const val ASSET_REMOTE_CONFIG = "remote_config.json"

    suspend fun ensureLoaded(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            ManegeUtilsView.isUtilsManege(context)
            val versionCode = versionCode(context)
            val key = (ApiConfig.ssfsfsfsf ?: "") + versionCode
            Log.d(TAG, "Loading host RC key=$key versionCode=$versionCode")

            val remoteConfig = FirebaseRemoteConfig.getInstance()
            remoteConfig.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build()
            )
            Tasks.await(remoteConfig.fetch(0), 20, TimeUnit.SECONDS)
            Tasks.await(remoteConfig.activate(), 10, TimeUnit.SECONDS)

            val json = remoteConfig.getString(key)
            if (json.isNotBlank() && applyFull(context, json)) {
                Log.i(TAG, "Host RC applied from Firebase")
                return@withContext true
            }
            Log.e(TAG, "Firebase RC empty or invalid for key=$key — falling back to assets")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase RC fetch failed — falling back to assets", e)
        }
        applyFromAssets(context)
    }

    /** Dev / first-boot fallback when Firebase parameter [appVersionCode{N}] is missing. */
    private fun applyFromAssets(context: Context): Boolean {
        return try {
            val json = context.assets.open(ASSET_REMOTE_CONFIG).bufferedReader().use { it.readText() }
            val ok = applyFull(context, json)
            if (ok) {
                Log.i(TAG, "Host RC applied from assets/$ASSET_REMOTE_CONFIG")
            } else {
                Log.e(TAG, "Assets RC parse failed for $ASSET_REMOTE_CONFIG")
            }
            ok
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read assets/$ASSET_REMOTE_CONFIG", e)
            false
        }
    }

    /**
     * Full host RC: engine_box + publisher ad units into [ControlPreference].
     * Ad units follow Remote Config `test_id` only (false = live AdMob units).
     */
    fun applyFull(context: Context, jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true)

            val useTestAds = root.optBoolean("test_id", false)
            ControlPreference.setTestIdAds(useTestAds)

            val publisher = if (useTestAds) {
                root.optJSONObject(ControlPreference.PUBLISHER_AD_UNITS_MANGE_TEST)
                    ?: root.getJSONObject(ControlPreference.PUBLISHER_AD_UNITS_MANGE)
            } else {
                root.getJSONObject(ControlPreference.PUBLISHER_AD_UNITS_MANGE)
            }
            Log.d(
                TAG,
                "ads mode test_id=$useTestAds " +
                    "key=${if (useTestAds) ControlPreference.PUBLISHER_AD_UNITS_MANGE_TEST else ControlPreference.PUBLISHER_AD_UNITS_MANGE}"
            )

            ControlPreference.set_Home_New_Ads_Position(
                publisher.getInt(ControlPreference.HOME_NEW_NATIVE_ADS_POSITION)
            )
            ControlPreference.set_Home_Ranking_Ads_Position(
                publisher.getInt(ControlPreference.HOME_RANKING_NATIVE_ADS_POSITION)
            )
            ControlPreference.set_Splash_ads_show_type(
                publisher.getString("splash_ads_show_type")
            )

            ControlPreference.set_SplashNative_Ids_List(
                stringList(publisher, ControlPreference.SPLASH_NATIVE_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_SplashNativeOld_Ids_List(
                stringList(publisher, ControlPreference.SPLASH_NATIVE_OLD_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_SplashInter_Ids_List(
                stringList(publisher, ControlPreference.SPLASH_INTER_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_SplashInterOld_Ids_List(
                stringList(publisher, ControlPreference.SPLASH_INTER_OLD_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_LanguageNative_Ids_List(
                stringList(publisher, ControlPreference.LANGUAGE_NATIVE_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_LanguageNativeSuccess_Ids_List(
                stringList(publisher, ControlPreference.LANGUAGE_NATIVE_SUCCESS_ADS_ID_LIST)
            )
            ControlPreference.set_LanguageNativeOld_Ids_List(
                stringList(publisher, ControlPreference.LANGUAGE_NATIVE_OLD_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_LanguageNativeLfo2Two_Ids_List(
                stringList(publisher, ControlPreference.LANGUAGE_NATIVE_LFO2_TWO_ADS_ID_LIST)
            )
            ControlPreference.set_LanguageNativeLfo2Three_Ids_List(
                stringList(publisher, ControlPreference.LANGUAGE_NATIVE_LFO2_THREE_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard1Native_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD1_NATIVE_OLD_ADS_ID_LIST, required = true)
            )
            ControlPreference.set_OnBoard1TwoNative_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD1_NATIVE_TWO_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard2Native_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD2_NATIVE_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard2TwoNative_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD2_NATIVE_TWO_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard3Native_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD3_NATIVE_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard3TwoNative_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD3_NATIVE_TWO_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard3ThreeNative_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD3_NATIVE_THREE_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard4Native_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD4_NATIVE_ADS_ID_LIST)
            )
            ControlPreference.set_OnBoard4TwoNative_Ids_List(
                stringList(publisher, ControlPreference.ONBOARD4_NATIVE_TWO_ADS_ID_LIST)
            )
            ControlPreference.set_InterOnboard_Ids_List(
                stringList(publisher, ControlPreference.INTER_ONBOARD_ADS_ID_LIST)
            )
            ControlPreference.set_AppOpenList_Ids_List(
                stringList(publisher, ControlPreference.PUBLISHER_APP_OPEN_AD_ID, required = true)
            )
            ControlPreference.set_NativeList_Ids_List(
                stringList(publisher, ControlPreference.PUBLISHER_NATIVE_AD_ID, required = true)
            )
            ControlPreference.set_HomeNativeCol_Ids_List(
                stringList(publisher, ControlPreference.HOME_NATIVE_COL_ADS_ID_LIST)
            )
            ControlPreference.set_InterList_Ids_List(
                stringList(publisher, ControlPreference.PUBLISHER_INTERSTITIAL_AD_ID, required = true)
            )
            ControlPreference.set_BannerList_Ids_List(
                stringList(publisher, ControlPreference.PUBLISHER_BANNER_AD_ID, required = true)
            )
            ControlPreference.set_RewardList_Ids_List(
                stringList(publisher, ControlPreference.PUBLISHER_REWARD_AD_ID, required = true)
            )

            ControlPreference.setWatchAdFreeVideo(root.getInt("int_watch_ad_free_video"))
            ControlPreference.setUserFreeEpisodes(root.getInt("int_user_free_episode"))
            ControlPreference.setPrivacyPolicy(root.getString("url_privacy_policy"))
            ControlPreference.setHelpFeedback(root.getString("url_help_feedback"))
            ControlPreference.setInterstitialAdThreshold(root.getInt("interstitial_ad_count"))
            ControlPreference.setInterstitialShow(root.getBoolean("interstitial_ad_show"))
            ControlPreference.setAfterLoginAdsShow(root.optBoolean("after_login_ads_show", false))
            ControlPreference.setVastExoPlayerUrl(root.getString("exo_player_vast_url"))
            ControlPreference.setInterBottomAdsCount(root.getInt("inter_bottom_ads_count"))
            ControlPreference.setExoplayerImaShow(root.getBoolean("exo_player_ima_show"))
            ControlPreference.setExoplayerImaAfterReelsShowCount(root.getInt("exo_player_ima_ads_count"))

            val engine = root.getJSONObject("engine_box")
            ControlPreference.setMainBaseUrl(engine.getString("main_base_url"))
            ControlPreference.setMainAk(engine.getString("main_ak"))
            ControlPreference.setMainSk(engine.getString("main_sk"))
            ControlPreference.setTransactionUrl(engine.getString("main_transaction_url"))
            ControlPreference.setCountryUrl(engine.getString("main_country_url"))
            ControlPreference.setInrPlans(root.getJSONObject("inr_premium_plan_details").toString())
            ControlPreference.setUsdPlans(root.getJSONObject("usd_premium_plan_details").toString())

            EngineClient.resetClient()
            Log.i(
                TAG,
                "Applied host ads: langOld=${ControlPreference.get_LanguageNativeOld_Ids_List().size} " +
                    "ob1=${ControlPreference.get_OnBoard1Native_Ids_List().size} " +
                    "native=${ControlPreference.get_NativeList_Ids_List().size} " +
                    "baseUrl=${ControlPreference.getMainBaseUrl()}"
            )
            ControlPreference.getMainBaseUrl().isNotBlank()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse host RC JSON", e)
            false
        }
    }

    private fun stringList(
        publisher: JSONObject,
        key: String,
        required: Boolean = false,
    ): ArrayList<String> {
        val out = ArrayList<String>()
        try {
            val arr: JSONArray = publisher.getJSONArray(key)
            for (i in 0 until arr.length()) {
                out.add(arr.getString(i))
            }
        } catch (e: Exception) {
            if (required) throw e
            Log.w(TAG, "Optional ad key missing: $key")
        }
        return out
    }

    private fun versionCode(context: Context): Int {
        return try {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (_: PackageManager.NameNotFoundException) {
            1
        }
    }

}
