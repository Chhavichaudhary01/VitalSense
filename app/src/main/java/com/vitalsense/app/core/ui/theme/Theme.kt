package com.vitalsense.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val VitalSenseColorScheme = lightColorScheme(
    primary = LimePrimary,
    onPrimary = TextPrimaryNearBlack,
    primaryContainer = LimePrimary.copy(alpha = 0.3f),
    onPrimaryContainer = TextPrimaryNearBlack,

    secondary = LavenderSecondary,
    onSecondary = TextPrimaryNearBlack,
    secondaryContainer = LavenderSecondary.copy(alpha = 0.3f),
    onSecondaryContainer = TextPrimaryNearBlack,

    tertiary = BlushPinkTertiary,
    onTertiary = TextPrimaryNearBlack,
    tertiaryContainer = BlushPinkTertiary.copy(alpha = 0.3f),
    onTertiaryContainer = TextPrimaryNearBlack,

    background = WarmCreamBackground,
    onBackground = TextPrimaryNearBlack,

    surface = SurfaceWhite,
    onSurface = TextPrimaryNearBlack,
    surfaceVariant = SurfaceCream,
    onSurfaceVariant = TextSecondaryMuted,

    error = CoralAlert,
    onError = SurfaceWhite,
    errorContainer = CoralAlert.copy(alpha = 0.15f),
    onErrorContainer = CoralAlert,

    outline = CardBorderColor,
    outlineVariant = CardBorderSubtle
)

@Composable
fun VitalSenseTheme(
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides VitalSenseSpacing(),
        LocalAppStrings provides AppLanguageManager.getStrings(language)
    ) {
        MaterialTheme(
            colorScheme = VitalSenseColorScheme,
            typography = VitalSenseTypography,
            shapes = VitalSenseShapes,
            content = content
        )
    }
}
