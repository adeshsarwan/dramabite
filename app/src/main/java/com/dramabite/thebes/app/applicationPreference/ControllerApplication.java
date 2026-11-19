package com.dramabite.thebes.app.applicationPreference;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.dramabite.thebes.app.tutorial.TutorialBootstrap;

public class ControllerApplication extends Application {


    private static ControllerApplication pref_instance = new ControllerApplication();



    public static ControllerApplication Instance() {
        if (pref_instance == null) {
            pref_instance = new ControllerApplication();
        }
        return pref_instance;
    }
    

    @Override
    public void onCreate() {
        super.onCreate();

        pref_instance = this;

        // Apply saved language on app startup
        String savedLanguage = ControlPreference.getLanguage();
        if (savedLanguage != null && !savedLanguage.isEmpty()) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(savedLanguage));
        }

        // AdSdk → TutorialSdk (UMP / MobileAds owned by ads stack on SDK Splash when used)
        TutorialBootstrap.INSTANCE.init(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }


}
