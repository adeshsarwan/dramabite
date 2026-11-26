package com.dramabite.thebes.app.tutorial

/**
 * AAP008 ads script units (live AdMob, from The Arrows' Adscript sheet).
 * "a" units in the sheet fill both the new-user (N) and returning-user (O) slots.
 */
object DramaBiteAdUnits {
    const val AD_101_SPL_N_NATIVE = "ca-app-pub-2256714742649688/9931826640"
    const val AD_101_SPL_O_NATIVE = "ca-app-pub-2256714742649688/9931826640"
    const val AD_102_SPL_N_INTER = "ca-app-pub-2256714742649688/2053336626"
    const val AD_102_SPL_O_INTER = "ca-app-pub-2256714742649688/2053336626"
    const val AD_103_ENDCARD_N_NATIVE = "ca-app-pub-2256714742649688/5801009947"
    const val AD_103_ENDCARD_O_NATIVE = "ca-app-pub-2256714742649688/5801009947"
    const val AD_201_LFO1_N_NATIVE = "ca-app-pub-2256714742649688/4002723443"
    const val AD_202_LFO2_N_NATIVE = "ca-app-pub-2256714742649688/1513572733"
    const val AD_203_LFO3_N_NATIVE = "ca-app-pub-2256714742649688/9063478435"
    const val AD_301_ONB1_N_NATIVE = "ca-app-pub-2256714742649688/8782396500"
    const val AD_303_ONB3_N_NATIVE_FULL = "ca-app-pub-2256714742649688/8399253129"
    const val AD_304_ONB4_N_NATIVE = "ca-app-pub-2256714742649688/3619580060"
    const val AD_305_ONB_N_INTER = "ca-app-pub-2256714742649688/3948164388"
    const val AD_401_WCB_O_NATIVE = "ca-app-pub-2256714742649688/6191892181"
    const val AD_401_WCB_O_INTER = "ca-app-pub-2256714742649688/4493678030"
    const val AD_401_HOME_A_NATIVE_COL = "ca-app-pub-2256714742649688/3693013510"

    // TODO(AAP008): ad-script placements with no Tutorial SDK slot in this codebase yet
    // (LFO/ONB returning-user, onboarding native, age screen). Home banner + home inter
    // are delivered through Remote Config (publisher_Banner_Ad_Id / publisher_Interstitial_Ad_Id).
    // Wire the rest once The Arrows confirms the SDK property names.
    const val AD_201_LFO1_O_NATIVE = "ca-app-pub-2256714742649688/5609438254"
    const val AD_202_LFO2_O_NATIVE = "ca-app-pub-2256714742649688/8044029906"
    const val AD_203_LFO3_O_NATIVE = "ca-app-pub-2256714742649688/7141303930"
    const val AD_301_ONB1_O_NATIVE = "ca-app-pub-2256714742649688/7277743141"
    const val AD_304_ONB4_O_NATIVE = "ca-app-pub-2256714742649688/3757300536"
    const val AD_305_ONB_A_NATIVE = "ca-app-pub-2256714742649688/1901293382"
    const val AD_306_AGE_N_NATIVE = "ca-app-pub-2256714742649688/3885293049"
    const val AD_401_HOME_A_BANNER_COL = "ca-app-pub-2256714742649688/5389946409"
    const val AD_401_HOME_A_INTER = "ca-app-pub-2256714742649688/2188360159"
}
