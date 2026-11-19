package com.dramabite.thebes.app.activity.language

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.dramabite.thebes.app.R
import com.dramabite.thebes.app.activity.main.base.BaseOtherActivity
import com.dramabite.thebes.app.ads.adsMenu.OnBoard.OnBoard3ThreeNativeAdView
import com.dramabite.thebes.app.ads.adsMenu.OnBoard.OnBoard4NativeAdView
import com.dramabite.thebes.app.applicationPreference.ControlPreference

class YoureAllSetActivity : BaseOtherActivity() {

    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youre_all_set)

        val linearSmallNtv = findViewById<LinearLayout>(R.id.linearSmallNtv)
        val nextButton = findViewById<TextView>(R.id.tvNext)

        // Display priority → native-ob3-3 (onboard_3_native_3) — IDs from RC only
        OnBoard3ThreeNativeAdView.adsViewNativeAds = 0
        OnBoard3ThreeNativeAdView.loadAdmobBigNativeAd(
            this,
            ControlPreference.get_OnBoard3ThreeNative_Ids_List(),
            linearSmallNtv
        )

        // Preload → native-ob4-1 / ob4-2 for Ready to Watch
        OnBoard4NativeAdView.adsViewNativeAds = 0
        OnBoard4NativeAdView.loadAdmobBigNativeAd(
            this,
            ControlPreference.get_OnBoard4Native_Ids_List(),
            null
        )

        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            navigateBack()
        }

        nextButton.setOnClickListener {
            navigateToReadyToWatch()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })
    }

    private fun navigateBack() {
        finish()
    }

    private fun navigateToReadyToWatch() {
        if (hasNavigated) {
            return
        }
        hasNavigated = true

        startActivity(Intent(this, ReadyToWatchActivity::class.java))
    }
}
