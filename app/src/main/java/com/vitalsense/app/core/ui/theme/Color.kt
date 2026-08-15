package com.vitalsense.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// --- Glume Reference Palette (Dark Mode Aesthetics) ---
val GlumeBackground = Color(0xFF0D0D12)       // Near-black main background
val GlumeSurfaceCard = Color(0xFF181822)      // Dark slate card surface
val GlumeSurfaceElevated = Color(0xFF22222F)  // Elevated interactive items / inner boxes
val GlumeSurfaceSubtle = Color(0xFF2C2C3C)    // Secondary pill / tag background
val GlumeBorder = Color(0xFF2A2A3A)           // Subtle border outline
val GlumeBorderSubtle = Color(0xFF1E1E2A)     // Hairline divider

// --- Glume Single Primary Accent Color ---
val GlumePrimaryPurple = Color(0xFF7C5CFC)    // Single primary purple CTA accent
val GlumePrimaryPurpleVariant = Color(0xFF6A47E5) // Pressed / darker purple
val GlumePrimaryPurpleContainer = Color(0x2E7C5CFC) // 18% opacity purple tint container
val GlumePrimaryPurpleLight = Color(0xFFA58FFF) // Light purple text on dark containers

// --- High Contrast Typography Tokens on Dark ---
val GlumeTextPrimary = Color(0xFFFFFFFF)      // Pure white header and primary values
val GlumeTextSecondary = Color(0xFF8E8E9F)    // Muted slate gray for supporting labels
val GlumeTextTertiary = Color(0xFF5E5E70)     // Subtle captions and timestamps

// --- Functional Status Colors (Glume-aligned Neon/Muted) ---
val GlumeSuccessMint = Color(0xFF2DD4BF)      // Neon teal/mint for completion & progress rings
val GlumeSuccessContainer = Color(0x262DD4BF) // Mint tint container
val GlumeSuccessText = Color(0xFF5EEAD4)

val GlumeAlertCoral = Color(0xFFFF5C5C)       // Neon coral for critical triage & SOS
val GlumeAlertContainer = Color(0x26FF5C5C)   // Coral tint container
val GlumeAlertText = Color(0xFFFF8A8A)

val GlumeWarningAmber = Color(0xFFFFB020)     // Amber warning for pending / moderate
val GlumeWarningContainer = Color(0x26FFB020)

// --- Patient High-Contrast Light Mode Tokens (Sunlight Readability) ---
val PatientLightBackground = Color(0xFFF6F6FA)
val PatientLightCard = Color(0xFFFFFFFF)
val PatientLightCardElevated = Color(0xFFF0F0F6)
val PatientLightBorder = Color(0xFFE2E2EC)
val PatientLightTextPrimary = Color(0xFF101018)
val PatientLightTextSecondary = Color(0xFF606072)

// --- Backward-Compatible Aliases (Mapped to Glume Palette) ---
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
val SoftMintText = GlumeSuccessText
val CoralAlert = GlumeAlertCoral
val CoralAlertDark = GlumeAlertText
val AmberWarning = GlumeWarningAmber
val AmberWarningDark = GlumeWarningAmber
val OrangeHighRisk = GlumeAlertCoral
val CardBorderColor = GlumeBorder
val CardBorderSubtle = GlumeBorderSubtle
val InputBorderColor = GlumeBorder
val InputBorderFocused = GlumePrimaryPurple
val DividerSubtle = GlumeBorderSubtle
