package com.vitalsense.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// --- NagarSeva Dark Color Scheme ---
private val NagarSevaDarkColorScheme = darkColorScheme(
    primary = NagarSevaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = NagarSevaPrimaryLight,

    secondary = NagarSevaElevatedDark,
    onSecondary = NagarSevaTextPrimaryDark,
    secondaryContainer = NagarSevaElevatedDark,
    onSecondaryContainer = NagarSevaTextSecondaryDark,

    tertiary = NagarSevaStatusNormal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = NagarSevaStatusNormal,

    background = NagarSevaCanvasDark,
    onBackground = NagarSevaTextPrimaryDark,

    surface = NagarSevaSurfaceDark,
    onSurface = NagarSevaTextPrimaryDark,
    surfaceVariant = NagarSevaElevatedDark,
    onSurfaceVariant = NagarSevaTextSecondaryDark,

    error = NagarSevaStatusUrgent,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = NagarSevaStatusUrgent,

    outline = NagarSevaBorderDark,
    outlineVariant = NagarSevaBorderDark
)

// --- NagarSeva Light Color Scheme (Default) ---
private val NagarSevaLightColorScheme = lightColorScheme(
    primary = NagarSevaPrimary,
    onPrimary = Color.White,
    primaryContainer = NagarSevaPrimaryContainer,
    onPrimaryContainer = NagarSevaPrimary,

    secondary = NagarSevaElevatedLight,
    onSecondary = NagarSevaTextPrimaryLight,
    secondaryContainer = NagarSevaElevatedLight,
    onSecondaryContainer = NagarSevaTextSecondaryLight,

    tertiary = NagarSevaStatusNormal,
    onTertiary = Color.White,
    tertiaryContainer = NagarSevaStatusNormalContainer,
    onTertiaryContainer = NagarSevaStatusNormalText,

    background = NagarSevaCanvasLight,
    onBackground = NagarSevaTextPrimaryLight,

    surface = NagarSevaSurfaceLight,
    onSurface = NagarSevaTextPrimaryLight,
    surfaceVariant = NagarSevaElevatedLight,
    onSurfaceVariant = NagarSevaTextSecondaryLight,

    error = NagarSevaStatusUrgent,
    onError = Color.White,
    errorContainer = NagarSevaStatusUrgentContainer,
    onErrorContainer = NagarSevaStatusUrgentText,

    outline = NagarSevaBorderLight,
    outlineVariant = NagarSevaBorderLight
)

@Composable
fun VitalSenseTheme(
    language: AppLanguage = AppLanguage.ENGLISH,
    usePatientLightMode: Boolean = true, // Default to Presentation Light Mode for clarity
    content: @Composable () -> Unit
) {
    val colorScheme = if (usePatientLightMode) NagarSevaLightColorScheme else NagarSevaDarkColorScheme

    CompositionLocalProvider(
        LocalSpacing provides VitalSenseSpacing(),
        LocalAppStrings provides AppLanguageManager.getStrings(language)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VitalSenseTypography,
            shapes = VitalSenseShapes,
            content = content
        )
    }
}
