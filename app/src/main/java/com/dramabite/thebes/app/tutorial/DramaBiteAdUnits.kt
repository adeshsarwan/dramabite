package com.dramabite.thebes.app.tutorial

/**
 * AAP008 (ShortDrama 13 / DramaMunch) ad script — live AdMob units for the kite tutorial SDK.
 * Kite slot ← ad-script unit name. "a" units serve every user; Language / Intro natives have a
 * first-pass unit ("n") and a `Repeat` unit ("o") for later passes. Remote Config `tut_*` keys
 * can override any of these (see kite_remote_config_AAP008.json).
 */
object DramaBiteAdUnits {
    // Splash
    const val SPLASH_NATIVE = "ca-app-pub-2256714742649688/9931826640"      // 101_spl_a_native
    const val SPLASH_INTER = "ca-app-pub-2256714742649688/2053336626"       // 102_spl_a_inter
    const val SPLASH_END_CARD = "ca-app-pub-2256714742649688/5801009947"    // 103_encdcard_a_native

    // Language
    const val LANGUAGE_LFO1 = "ca-app-pub-2256714742649688/4002723443"        // 201_lfo1_n_native
    const val LANGUAGE_LFO1_REPEAT = "ca-app-pub-2256714742649688/5609438254" // 201_lfo1_o_native
    const val LANGUAGE_LFO2 = "ca-app-pub-2256714742649688/1513572733"        // 202_lfo2_n_native
    const val LANGUAGE_LFO2_REPEAT = "ca-app-pub-2256714742649688/8044029906" // 202_lfo2_o_native
    const val LANGUAGE_LFO3 = "ca-app-pub-2256714742649688/9063478435"        // 203_lfo3_n_native
    const val LANGUAGE_LFO3_REPEAT = "ca-app-pub-2256714742649688/7141303930" // 203_lfo3_o_native

    // Intro (onboarding)
    const val INTRO_ONB1 = "ca-app-pub-2256714742649688/8782396500"         // 301_onb1_n_native
    const val INTRO_ONB1_REPEAT = "ca-app-pub-2256714742649688/7277743141"  // 301_onb1_o_native
    const val INTRO_FULL = "ca-app-pub-2256714742649688/8399253129"         // 303_onb3_a_native_full
    const val INTRO_ONB4 = "ca-app-pub-2256714742649688/3619580060"         // 304_onb4_n_native
    const val INTRO_ONB4_REPEAT = "ca-app-pub-2256714742649688/3757300536"  // 304_onb4_o_native
    const val INTRO_INTER = "ca-app-pub-2256714742649688/3948164388"        // 305_onb_a_inter
    const val INTRO_END_CARD = "ca-app-pub-2256714742649688/1901293382"     // 305_onb_a_native

    // Age check
    const val AGE_NATIVE = "ca-app-pub-2256714742649688/3885293049"         // 306_age_n_native

    // Welcome Back
    const val WELCOME_BACK_NATIVE = "ca-app-pub-2256714742649688/6191892181" // 401_wcb_o_native
    const val WELCOME_BACK_INTER = "ca-app-pub-2256714742649688/4493678030"  // 401_wcb_o_inter

    // Home (host-owned, not part of kite)
    const val AD_401_HOME_A_NATIVE_COL = "ca-app-pub-2256714742649688/3693013510" // collapsible native
    const val AD_401_HOME_A_BANNER_COL = "ca-app-pub-2256714742649688/5389946409" // via RC publisher_Banner_Ad_Id
    const val AD_401_HOME_A_INTER = "ca-app-pub-2256714742649688/2188360159"      // via RC publisher_Interstitial_Ad_Id
}
