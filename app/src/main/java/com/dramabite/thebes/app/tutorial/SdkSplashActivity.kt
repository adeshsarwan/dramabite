package com.dramabite.thebes.app.tutorial

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.tutorial.kite.splash.KiteSplashActivity
import kotlinx.coroutines.CompletableDeferred

/**
 * Host work on the kite Splash: load the host Remote Config (content API, in-app ad ids) and ask
 * POST_NOTIFICATIONS on API 33+. Kite runs this once UMP consent has settled; the Splash
 * interstitial and the hand-off wait for it.
 */
class SdkSplashActivity : KiteSplashActivity() {

    private val permissionResult = CompletableDeferred<Boolean>()

    // Registered as a field: registerForActivityResult must run before STARTED.
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionResult.complete(granted)
        }

    /** True while the system dialog is up; survives recreation so the request is not launched twice. */
    private var asking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        asking = savedInstanceState?.getBoolean(KEY_ASKING) == true
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ASKING, asking)
    }

    override suspend fun onSplashWork() {
        val rcOk = HostRemoteConfig.ensureLoaded(applicationContext)
        Log.i(TAG, "HostRemoteConfig loaded=$rcOk testAds=${ControlPreference.getTestIdAds()}")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) return
        if (!asking) {
            asking = true
            requestPermission.launch(permission)
        }
        val granted = permissionResult.await()
        asking = false
        Log.i(TAG, "POST_NOTIFICATIONS granted=$granted")
    }

    private companion object {
        const val TAG = "SdkSplash"
        const val KEY_ASKING = "dramabite_splash_asking_permission"
    }
}
