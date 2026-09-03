package com.vitalsense.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// --- NagarSeva Design System Palette ---
// Canvas & Surfaces
val NagarSevaCanvasLight = Color(0xFFF8F9FA)      // Soft neutral canvas
val NagarSevaCanvasDark = Color(0xFF0F172A)       // Midnight slate canvas
val NagarSevaSurfaceLight = Color(0xFFFFFFFF)     // Crisp white elevated card
val NagarSevaSurfaceDark = Color(0xFF1E293B)      // Slate 800 card
val NagarSevaElevatedLight = Color(0xFFF1F5F9)    // Slate 100 inner elevation
val NagarSevaElevatedDark = Color(0xFF334155)     // Slate 700 inner elevation
val NagarSevaBorderLight = Color(0xFFE2E8F0)      // Subtle hairline border (Slate 200)
val NagarSevaBorderDark = Color(0xFF334155)       // Dark border outline

// Primary Accent: Electric Violet / Indigo
val NagarSevaPrimary = Color(0xFF7C5CFF)          // Electric Violet primary CTA
val NagarSevaPrimaryVariant = Color(0xFF6366F1)   // Indigo accent variant
val NagarSevaPrimaryContainer = Color(0xFFEEF2FF) // Soft indigo container tint
val NagarSevaPrimaryLight = Color(0xFFA58FFF)     // Lavender text accent

// High-Contrast Typography Tokens
val NagarSevaTextPrimaryLight = Color(0xFF0F172A) // Slate 900 (High contrast)
val NagarSevaTextPrimaryDark = Color(0xFFF8FAFC)  // Slate 50
val NagarSevaTextSecondaryLight = Color(0xFF64748B) // Slate 500
val NagarSevaTextSecondaryDark = Color(0xFF94A3B8)  // Slate 400
val NagarSevaTextTertiary = Color(0xFFCBD5E1)     // Slate 300

// Status Badges & Alerts
val NagarSevaStatusUrgent = Color(0xFFEF4444)     // Red 500
val NagarSevaStatusUrgentContainer = Color(0xFFFEE2E2) // Red 100
val NagarSevaStatusUrgentText = Color(0xFFDC2626) // Red 600

val NagarSevaStatusProgress = Color(0xFFF59E0B)   // Amber 500
val NagarSevaStatusProgressContainer = Color(0xFFFEF3C7) // Amber 100
val NagarSevaStatusProgressText = Color(0xFFD97706) // Amber 600

val NagarSevaStatusNormal = Color(0xFF10B981)     // Emerald 500
val NagarSevaStatusNormalContainer = Color(0xFFD1FAE5) // Emerald 100
val NagarSevaStatusNormalText = Color(0xFF059669) // Emerald 600

val NagarSevaStatusNormalBg = NagarSevaStatusNormalContainer
val NagarSevaStatusProgressBg = NagarSevaStatusProgressContainer
val NagarSevaStatusUrgentBg = NagarSevaStatusUrgentContainer

val GlumeAlertAmber = NagarSevaStatusProgress
val GlumeAlertAmberContainer = NagarSevaStatusProgressContainer

// Glume Aliases Mapped to NagarSeva System for 100% Zero-Regression
val GlumeBackground = NagarSevaCanvasLight
val GlumeSurfaceCard = NagarSevaSurfaceLight
val GlumeSurfaceElevated = NagarSevaElevatedLight
val GlumeSurfaceSubtle = NagarSevaPrimaryContainer
val GlumeBorder = NagarSevaBorderLight
val GlumeBorderSubtle = NagarSevaBorderLight

val GlumePrimaryPurple = NagarSevaPrimary
val GlumePrimaryPurpleVariant = NagarSevaPrimaryVariant
val GlumePrimaryPurpleContainer = NagarSevaPrimaryContainer
val GlumePrimaryPurpleLight = NagarSevaPrimaryLight

val GlumeTextPrimary = NagarSevaTextPrimaryLight
val GlumeTextSecondary = NagarSevaTextSecondaryLight
val GlumeTextTertiary = NagarSevaTextTertiary

val GlumeSuccessMint = NagarSevaStatusNormal
val GlumeSuccessContainer = NagarSevaStatusNormalContainer
val GlumeSuccessText = NagarSevaStatusNormalText

val GlumeAlertCoral = NagarSevaStatusUrgent
val GlumeAlertContainer = NagarSevaStatusUrgentContainer
val GlumeAlertText = NagarSevaStatusUrgentText

val GlumeWarningAmber = NagarSevaStatusProgress
val GlumeWarningContainer = NagarSevaStatusProgressContainer
val GlumeWarningText = NagarSevaStatusProgressText

val GlumeError = GlumeAlertCoral
val GlumeErrorContainer = GlumeAlertContainer
val GlumeWarning = GlumeWarningAmber
val GlumePrimaryBlue = GlumePrimaryPurple
val StatusSafeGreen = GlumeSuccessMint
val StatusAttentionAmber = GlumeWarningAmber
val StatusDangerRed = GlumeAlertCoral

// Presentation Tokens
val VitalSenseTealPrimary = NagarSevaPrimary
val VitalSenseTealContainer = NagarSevaPrimaryContainer
val PresentationLightBackground = NagarSevaCanvasLight
val PresentationLightCard = NagarSevaSurfaceLight
val PresentationLightCardElevated = NagarSevaElevatedLight
val PresentationLightBorder = NagarSevaBorderLight
val PresentationLightTextPrimary = NagarSevaTextPrimaryLight
val PresentationLightTextSecondary = NagarSevaTextSecondaryLight

// Aliases for compatibility
val PatientLightBackground = PresentationLightBackground
val PatientLightCard = PresentationLightCard
val PatientLightCardElevated = PresentationLightCardElevated
val PatientLightBorder = PresentationLightBorder
val PatientLightTextPrimary = PresentationLightTextPrimary
val PatientLightTextSecondary = PresentationLightTextSecondary

val LimePrimary = GlumePrimaryPurple
val DarkCharcoal = GlumeSurfaceElevated
val LavenderSecondary = GlumeSurfaceSubtle
val BlushPinkTertiary = GlumeSurfaceElevated
val WarmCreamBackground = GlumeBackground
val SurfaceWhite = GlumeSurfaceCard
val SurfaceCream = GlumeSurfaceElevated
val TextPrimaryNearBlack = GlumeTextPrimary
val TextSecondaryMuted = GlumeTextSecondary
val TextTertiarySubtle = GlumeTextTertiary
val SoftMintSuccess = GlumeSuccessMint
val CoralAlert = GlumeAlertCoral
val AmberWarning = GlumeWarningAmber
val SoftMint = GlumeSuccessMint
val SlateCard = GlumeSurfaceCard
