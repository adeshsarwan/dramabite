package com.dramabite.thebes.app.tutorial

import android.Manifest
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.dramabite.thebes.app.R
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.tutorial.TutorialSdk
import com.tutorial.screen.splash.SplashActivity
import com.tutorial.screen.splash.SplashContent
import com.tutorial.screen.splash.SplashEffect
import com.tutorial.screen.splash.SplashIntent
import com.tutorial.screen.splash.SplashState
import kotlinx.coroutines.CompletableDeferred

/**
 * SDK Splash branded with DramaBite [Theme]. Uses SDK [SplashContent] so native/inter ads
 * stay wired. Splash icon is forced to [R.drawable.icon_round] via
 * [getApplicationInfo] so [com.tutorial.ui.rememberAppBranding] picks it up (not the launcher mipmap).
 */
class SdkSplashActivity : SplashActivity() {

    private val permissionResult = CompletableDeferred<Boolean>()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionResult.complete(granted)
        }

    /**
     * Tutorial SDK [rememberAppBranding] loads `context.applicationInfo.loadIcon(pm)`.
     * Point that icon at [R.drawable.icon_round] for this activity only.
     */
    override fun getApplicationInfo(): ApplicationInfo {
        return ApplicationInfo(super.getApplicationInfo()).apply {
            icon = R.drawable.icon_round
        }
    }

    @Composable
    override fun Theme(content: @Composable () -> Unit) {
        DramaBiteTutorialTheme.Apply(content)
    }

    @Composable
    override fun Content(state: SplashState, onIntent: (SplashIntent) -> Unit) {
        SplashContent(state, onIntent)
    }

    override suspend fun onSplashWork() {
        val rcOk = HostRemoteConfig.ensureLoaded(applicationContext)
        Log.i(
            TAG,
            "HostRemoteConfig loaded=$rcOk testAds=${ControlPreference.getTestIdAds()} " +
                "baseUrl=${ControlPreference.getMainBaseUrl()} " +
                "langOldIds=${ControlPreference.get_LanguageNativeOld_Ids_List().size}"
        )

        // UMP / ads consent must complete before any system permission dialog.
        awaitConsent()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestPermission.launch(permission)
        val granted = permissionResult.await()
        Log.i(TAG, "POST_NOTIFICATIONS granted=$granted")
    }

    /**
     * Resume mid first-open:
     * Language done → SDK Onboarding; SDK onboarding / login done → Home (SDK default).
     */
    override fun onEffect(effect: SplashEffect): Boolean {
        if (effect !is SplashEffect.NavigateNext) return false

        val languageDone = ControlPreference.getLanguageScreen()
        val sdkOnboardingDone = ControlPreference.getSdkOnboardingScreen()
        val loginDone = ControlPreference.getLoginScreen()
        val sdkOnboarded = TutorialSdk.prefs.isOnboarded

        if (sdkOnboarded && !loginDone) {
            TutorialSdk.prefs.isOnboarded = false
            Log.i(TAG, "cleared premature isOnboarded")
        }

        // First-open finished (or login skipped via SDK onboarding → Home)
        if (loginDone || sdkOnboardingDone) return false

        if (languageDone && !sdkOnboardingDone) {
            Log.i(TAG, "resume_sdk_onboarding")
            startActivity(Intent(this, SdkOnboardingActivity::class.java))
            finish()
            return true
        }

        return false
    }

    private companion object {
        const val TAG = "SdkSplash"
    }
}
