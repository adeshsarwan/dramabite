package com.dramabite.thebes.app.activity.language

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.dramabite.thebes.app.R
import com.dramabite.thebes.app.activity.main.base.BaseOtherActivity
import com.dramabite.thebes.app.ads.adsMenu.OnBoard.OnBoard1TwoNativeAdView
import com.dramabite.thebes.app.applicationPreference.ControlPreference

class PreferencesSavedActivity : BaseOtherActivity() {

    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences_saved)

        val linearSmallNtv = findViewById<LinearLayout>(R.id.linearSmallNtv)
        val nextButton = findViewById<TextView>(R.id.tvNext)

        // Display → native-ob1-2 (onboard_1_native_2) — IDs from RC only
        OnBoard1TwoNativeAdView.adsViewNativeAds = 0
        OnBoard1TwoNativeAdView.loadAdmobBigNativeAd(
            this,
            ControlPreference.get_OnBoard1TwoNative_Ids_List(),
            linearSmallNtv
        )

        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            navigateBack()
        }

        nextButton.setOnClickListener {
            navigateToHomepageFeatures()
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

    private fun navigateToHomepageFeatures() {
        if (hasNavigated) {
            return
        }
        hasNavigated = true

        startActivity(Intent(this, HomepageFeaturesActivity::class.java))
    }
}
