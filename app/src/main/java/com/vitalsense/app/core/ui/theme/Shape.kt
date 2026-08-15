package com.vitalsense.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val VitalSenseShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),    // Standard cards
    large = RoundedCornerShape(20.dp),     // Dialogs & Modals
    extraLarge = RoundedCornerShape(50.dp) // Full Pill buttons/chips
)

val CardShape = RoundedCornerShape(16.dp)
val PillShape = RoundedCornerShape(50.dp)
val InputShape = RoundedCornerShape(12.dp)
val DialogShape = RoundedCornerShape(20.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
