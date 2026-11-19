package com.dramabite.thebes.app.activity.main.activity

import android.content.Intent
import com.dramabite.thebes.app.activity.language.LanguageActivity
import com.dramabite.thebes.app.activity.login.LoginMainActivity
import com.dramabite.thebes.app.activity.main.base.BaseActivity
import com.dramabite.thebes.app.applicationPreference.ControlPreference


class SplashScreenActivity : BaseActivity() {
    override fun initActivity() {
        goToMain()
    }

    private fun goToMain() {
        ControlPreference.set_in_splash_first_time(false)
        if (!ControlPreference.getLanguageScreen()) {
            startActivity(Intent(this, LanguageActivity::class.java))
        } else if (!ControlPreference.getLoginScreen()) {
            startActivity(Intent(this, LoginMainActivity::class.java).putExtra("ForWardScreenName", "SplashScreenActivity"))
        } else {
            startActivity(
                Intent(this, HomeScreenActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }

        finish()
    }

}