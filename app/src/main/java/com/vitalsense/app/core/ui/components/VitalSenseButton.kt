package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.theme.*

enum class ButtonStyle {
    PRIMARY,   // Glume Single Primary Purple (#7C5CFC) with white text
    DARK,      // Glume Elevated Slate (#22222F)
    SECONDARY, // Glume Surface Subtle (#2C2C3C)
    DANGER,    // Glume Alert Coral (#FF5C5C)
    OUTLINED   // Transparent with subtle border
}

@Composable
fun VitalSenseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    icon: (@Composable () -> Unit)? = null,
    minHeight: Dp = 48.dp,
    enabled: Boolean = true
) {
    val containerColor = when (style) {
        ButtonStyle.PRIMARY -> GlumePrimaryPurple
        ButtonStyle.DARK -> GlumeSurfaceElevated
        ButtonStyle.SECONDARY -> GlumeSurfaceSubtle
        ButtonStyle.DANGER -> GlumeAlertCoral
        ButtonStyle.OUTLINED -> Color.Transparent
    }

    val contentColor = when (style) {
        ButtonStyle.PRIMARY -> GlumeTextPrimary
        ButtonStyle.DARK -> GlumeTextPrimary
        ButtonStyle.SECONDARY -> GlumeTextPrimary
        ButtonStyle.DANGER -> GlumeTextPrimary
        ButtonStyle.OUTLINED -> GlumeTextPrimary
    }

    val border = if (style == ButtonStyle.OUTLINED) {
        BorderStroke(1.dp, GlumeBorder)
    } else null

    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .fillMaxWidth(),
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = GlumeSurfaceElevated.copy(alpha = 0.5f),
            disabledContentColor = GlumeTextTertiary
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(Spacing.xs))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
