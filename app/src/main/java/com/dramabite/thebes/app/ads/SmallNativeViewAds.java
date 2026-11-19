package com.dramabite.thebes.app.ads;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.dramabite.thebes.app.R;

public class SmallNativeViewAds extends LinearLayout {

    static LinearLayout linearMediumAds;
    static ShimmerFrameLayout shimmerAdsLayout;
    static Context context;

    public SmallNativeViewAds(Context nrps__context) {
        super(nrps__context);
    }

    public SmallNativeViewAds(Context nrps__context, @Nullable AttributeSet attrs) {
        super(nrps__context, attrs);
        SmallNativeViewAds.context = nrps__context;
        if (isInEditMode()) {
            return;
        }
        // Gifts / top-up / premium screens stay off while after_login_ads_show is false.
        // Home calls bindHomeCollapsible() instead of this path.
        if (PremiumPlanManager.shouldSkipAfterLoginAd(nrps__context)) {
            setVisibility(View.GONE);
            return;
        }
        LayoutInflater li = LayoutInflater.from(nrps__context);
        li.inflate(R.layout.ads_small_native, this);
        linearMediumAds = (LinearLayout) findViewById(R.id.linearSmallNtv);
        shimmerAdsLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerAdsLayout);
        NativeAdsManege.nrps__Load_Admob_Big_NativeAd(context, linearMediumAds ,shimmerAdsLayout,"small");
    }

    /** Home bottom slot only. Premium hides it; after_login_ads_show does not. */
    public void bindHomeCollapsible() {
        Context host = getContext();
        if (host == null || PremiumPlanManager.isPremiumActive(host)) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        if (linearMediumAds == null || shimmerAdsLayout == null) {
            removeAllViews();
            LayoutInflater.from(host).inflate(R.layout.ads_small_native, this);
            linearMediumAds = findViewById(R.id.linearSmallNtv);
            shimmerAdsLayout = findViewById(R.id.shimmerAdsLayout);
        }
        NativeAdsManege.loadHomeCollapsibleNative(host, linearMediumAds, shimmerAdsLayout);
    }

    public SmallNativeViewAds(Context nrps__context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(nrps__context, attrs, defStyleAttr);
    }

}