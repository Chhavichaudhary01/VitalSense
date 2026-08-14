package com.vitalsense.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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

    outline = Color(0xFFE5DECE),
    outlineVariant = Color(0xFFF0EAE0)
)

@Composable
fun VitalSenseTheme(
    darkTheme: Boolean = false, // Rural high-contrast light theme as per design doc §2.2
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides VitalSenseSpacing()
    ) {
        MaterialTheme(
            colorScheme = VitalSenseColorScheme,
            typography = VitalSenseTypography,
            shapes = VitalSenseShapes,
            content = content
        )
    }
}
