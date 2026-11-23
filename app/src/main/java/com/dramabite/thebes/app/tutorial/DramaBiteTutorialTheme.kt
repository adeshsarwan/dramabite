package com.dramabite.thebes.app.tutorial

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tutorial.ui.theme.TutorialTheme

/**
 * Light (white) theme for Splash / Language / Onboarding.
 */
object DramaBiteTutorialTheme {
    val BrandPrimary = Color(0xFFF05A1A)
    val Background = Color(0xFFFFFFFF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF5F5F5)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B6B6B)
    val Outline = Color(0xFFE0E0E0)

    /** Splash uses the same navy as onboarding, not white. */
    @Composable
    fun Apply(content: @Composable () -> Unit) {
        val backdrop = Color(0xFF10083A)
        TutorialTheme(
            colors = TutorialTheme.colors.copy(
                primary = BrandPrimary,
                onPrimary = Color.White,
                background = backdrop,
                splashBackground = backdrop,
                splashText = Color.White,
                surface = backdrop,
                surfaceVariant = Color(0xFF241C3D),
                onSurface = Color.White,
                onSurfaceVariant = Color(0xFFC9C2D6),
                outline = Color(0xFF3A3154),
                endCardCta = BrandPrimary,
                endCardOnCta = Color.White,
            ),
            content = content,
        )
    }

    /** Darker language screen so it sits with the onboarding art. */
    @Composable
    fun ApplyLanguage(content: @Composable () -> Unit) {
        TutorialTheme(
            colors = TutorialTheme.colors.copy(
                primary = BrandPrimary,
                onPrimary = Color.White,
                background = Color(0xFF16102C),
                surface = Color(0xFF241C3D),
                surfaceVariant = Color(0xFF2C2348),
                onSurface = Color.White,
                onSurfaceVariant = Color(0xFFC9C2D6),
                outline = Color(0xFF3A3154),
                languageBackground = Color(0xFF16102C),
                languageText = Color.White,
                languageTextSecondary = Color(0xFFC9C2D6),
                languageRowBackground = Color(0xFF241C3D),
            ),
            content = content,
        )
    }

    /** Onboarding backdrop matches the native ad card (#10083A). */
    @Composable
    fun ApplyOnboarding(content: @Composable () -> Unit) {
        val backdrop = Color(0xFF10083A)
        TutorialTheme(
            colors = TutorialTheme.colors.copy(
                primary = BrandPrimary,
                onPrimary = Color.White,
                background = backdrop,
                onboardingBackground = backdrop,
                surface = backdrop,
                surfaceOverlay = Color.Transparent,
                surfaceVariant = Color(0xFF241C3D),
                onSurface = Color.White,
                onSurfaceVariant = Color(0xFFC9C2D6),
                outline = Color(0xFF3A3154),
                // AdSlot recolors card / headline / CTA / badge from these — must stay in
                // CompositionLocal when we wrap with copy(); media letterbox is XML only.
                nativeAdBackground = backdrop,
                nativeAdText = Color.White,
                nativeAdCta = BrandPrimary,
                nativeAdOnCta = Color.White,
                nativeAdBadge = BrandPrimary,
                nativeAdOnBadge = Color.White,
            ),
            content = content,
        )
    }
}
