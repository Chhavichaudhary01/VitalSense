package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.ui.theme.*

enum class ButtonStyle {
    PRIMARY,   // Lime background with near-black text
    DARK,      // Charcoal pill
    SECONDARY, // Lavender
    DANGER,    // Coral / SOS
    OUTLINED   // Transparent with border
}

@Composable
fun VitalSenseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    icon: (@Composable () -> Unit)? = null,
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
        BorderStroke(1.5.dp, TextPrimaryNearBlack)
    } else null

    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth(),
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color(0xFFE0DDD5),
            disabledContentColor = TextSecondaryMuted
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }
    }
}
