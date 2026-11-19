package com.dramabite.thebes.app.tutorial

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.core.os.LocaleListCompat
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.tutorial.TutorialSdk
import com.tutorial.screen.language.LanguageActivity
import com.tutorial.screen.language.LanguageContent
import com.tutorial.screen.language.LanguageEffect
import com.tutorial.screen.language.LanguageIntent
import com.tutorial.screen.language.LanguageState

/**
 * SDK Language (LFO ads) with DramaBite brand. After confirm, applies host language prefs
 * then lets the SDK open [SdkOnboardingActivity] → InterOnboarding → Home.
 */
class SdkLanguageActivity : LanguageActivity() {

    @Composable
    override fun Theme(content: @Composable () -> Unit) {
        DramaBiteTutorialTheme.ApplyLanguage(content)
    }

    @Composable
    override fun Content(state: LanguageState, onIntent: (LanguageIntent) -> Unit) {
        LanguageContent(state, onIntent)
    }

    override fun onEffect(effect: LanguageEffect): Boolean {
        if (effect !is LanguageEffect.NavigateNext) return false

        val code = viewModel.state.value.selectedCode
            ?.takeIf { it.isNotBlank() }
            ?: TutorialSdk.languageCode
            ?: "en"
        val shortCode = code.substringBefore('-').ifBlank { "en" }

        Log.d(TAG, "language_confirmed code=$code → SDK Onboarding")

        ControlPreference.setAppLanguage(shortCode)
        ControlPreference.setLanguage(shortCode)
        ControlPreference.setLanguageScreen(true)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(shortCode))

        // Do not open host screens here; do not set isOnboarded.
        // return false → SDK navigator continues to Onboarding.
        return false
    }

    private companion object {
        const val TAG = "SdkLanguage"
    }
}
