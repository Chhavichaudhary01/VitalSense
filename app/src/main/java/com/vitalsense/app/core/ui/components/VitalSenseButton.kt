package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.theme.*

enum class ButtonStyle {
    PRIMARY,   // Lime background with near-black text
    DARK,      // Charcoal pill with Lime text
    SECONDARY, // Lavender background
    DANGER,    // Coral / SOS
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
        ButtonStyle.PRIMARY -> LimePrimary
        ButtonStyle.DARK -> DarkCharcoal
        ButtonStyle.SECONDARY -> LavenderSecondary
        ButtonStyle.DANGER -> CoralAlert
        ButtonStyle.OUTLINED -> Color.Transparent
    }

    val contentColor = when (style) {
        ButtonStyle.PRIMARY -> TextPrimaryNearBlack
        ButtonStyle.DARK -> LimePrimary
        ButtonStyle.SECONDARY -> TextPrimaryNearBlack
        ButtonStyle.DANGER -> SurfaceWhite
        ButtonStyle.OUTLINED -> TextPrimaryNearBlack
    }

    val border = if (style == ButtonStyle.OUTLINED) {
        BorderStroke(1.5.dp, DarkCharcoal)
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
            disabledContainerColor = Color(0xFFE4DFD5),
            disabledContentColor = TextSecondaryMuted
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
