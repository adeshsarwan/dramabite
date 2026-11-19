package com.dramabite.thebes.app.tutorial

import android.util.Log
import androidx.compose.runtime.Composable
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.tutorial.screen.onboarding.OnboardingActivity
import com.tutorial.screen.onboarding.OnboardingContent
import com.tutorial.screen.onboarding.OnboardingEffect
import com.tutorial.screen.onboarding.OnboardingIntent
import com.tutorial.screen.onboarding.OnboardingState

/**
 * SDK Onboarding (branded). After last page / skip the SDK shows InterOnboarding,
 * then [OnboardingEffect.NavigateNext] — we mark first-open done and let the SDK
 * open [homeActivity] (HomeScreenActivity). No host LanguagePreference / Login chain.
 */
class SdkOnboardingActivity : OnboardingActivity() {

    @Composable
    override fun Theme(content: @Composable () -> Unit) {
        DramaBiteTutorialTheme.ApplyOnboarding(content)
    }

    @Composable
    override fun Content(state: OnboardingState, onIntent: (OnboardingIntent) -> Unit) {
        OnboardingContent(state, onIntent)
    }

    override fun onEffect(effect: OnboardingEffect): Boolean {
        if (effect !is OnboardingEffect.NavigateNext) return false

        Log.d(TAG, "sdk_onboarding_done → SDK inter then Home")
        ControlPreference.setSdkOnboardingScreen(true)
        // First-open complete so splash resume does not clear isOnboarded / reopen LFO
        ControlPreference.setLoginScreen(true)
        // false → SDK navigateNext() → HomeScreenActivity (after InterOnboarding already shown)
        return false
    }

    private companion object {
        const val TAG = "SdkOnboarding"
    }
}
