package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.theme.CardShape
import com.vitalsense.app.core.ui.theme.SurfaceWhite

@Composable
fun VitalSenseCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceWhite,
    elevation: Dp = 2.dp,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = CardShape,
        color = backgroundColor,
        shadowElevation = elevation,
        border = border
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
