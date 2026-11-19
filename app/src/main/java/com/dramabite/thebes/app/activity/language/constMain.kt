package com.dramabite.thebes.app.activity.language

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.dramabite.thebes.app.applicationPreference.ControlPreference

import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun Context.setLocale(str: String) {
    ControlPreference.setAppLanguage(str)
    
    // Apply language using AndroidX AppCompatDelegate (Modern approach)
    val appLocale = LocaleListCompat.forLanguageTags(str)
    AppCompatDelegate.setApplicationLocales(appLocale)

    // Keep this for any non-Android Java components that rely on default Locale
    Locale.setDefault(Locale(str))
}
