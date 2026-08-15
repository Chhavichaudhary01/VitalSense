package com.vitalsense.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val VitalSenseShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),    // Glume rounded cards (18dp)
    large = RoundedCornerShape(24.dp),     // Glume dialogs & modals (24dp)
    extraLarge = RoundedCornerShape(50.dp) // Glume full Pill buttons/chips
)

val CardShape = RoundedCornerShape(18.dp)
val StatCardShape = RoundedCornerShape(16.dp)
val PillShape = RoundedCornerShape(50.dp)
val InputShape = RoundedCornerShape(14.dp)
val DialogShape = RoundedCornerShape(24.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
