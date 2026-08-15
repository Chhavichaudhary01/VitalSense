package com.vitalsense.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// --- Glume Dark Color Scheme (Default for Doctor, ASHA, Admin) ---
private val GlumeDarkColorScheme = darkColorScheme(
    primary = GlumePrimaryPurple,
    onPrimary = GlumeTextPrimary,
    primaryContainer = GlumePrimaryPurpleContainer,
    onPrimaryContainer = GlumePrimaryPurpleLight,

    secondary = GlumeSurfaceElevated,
    onSecondary = GlumeTextPrimary,
    secondaryContainer = GlumeSurfaceSubtle,
    onSecondaryContainer = GlumeTextSecondary,

    tertiary = GlumeSuccessMint,
    onTertiary = GlumeTextPrimary,
    tertiaryContainer = GlumeSuccessContainer,
    onTertiaryContainer = GlumeSuccessText,

    background = GlumeBackground,
    onBackground = GlumeTextPrimary,

    surface = GlumeSurfaceCard,
    onSurface = GlumeTextPrimary,
    surfaceVariant = GlumeSurfaceElevated,
    onSurfaceVariant = GlumeTextSecondary,

    error = GlumeAlertCoral,
    onError = GlumeTextPrimary,
    errorContainer = GlumeAlertContainer,
    onErrorContainer = GlumeAlertText,

    outline = GlumeBorder,
    outlineVariant = GlumeBorderSubtle
)

// --- Patient High-Contrast Light Color Scheme (Optional Sunlight Mode) ---
private val PatientLightColorScheme = lightColorScheme(
    primary = GlumePrimaryPurple,
    onPrimary = GlumeTextPrimary,
    primaryContainer = GlumePrimaryPurpleContainer,
    onPrimaryContainer = GlumePrimaryPurple,

    secondary = PatientLightCardElevated,
    onSecondary = PatientLightTextPrimary,
    secondaryContainer = PatientLightCardElevated,
    onSecondaryContainer = PatientLightTextSecondary,

    tertiary = GlumeSuccessMint,
    onTertiary = PatientLightTextPrimary,

    background = PatientLightBackground,
    onBackground = PatientLightTextPrimary,

    surface = PatientLightCard,
    onSurface = PatientLightTextPrimary,
    surfaceVariant = PatientLightCardElevated,
    onSurfaceVariant = PatientLightTextSecondary,

    error = GlumeAlertCoral,
    onError = GlumeTextPrimary,

    outline = PatientLightBorder,
    outlineVariant = PatientLightBorder
)

@Composable
fun VitalSenseTheme(
    language: AppLanguage = AppLanguage.ENGLISH,
    usePatientLightMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (usePatientLightMode) PatientLightColorScheme else GlumeDarkColorScheme

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
