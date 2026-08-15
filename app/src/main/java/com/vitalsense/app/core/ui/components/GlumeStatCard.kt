package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.ui.theme.*

/**
 * Glume Stat Display Pattern (from reference screenshot):
 * Compact grid card displaying small icon + label + bold value.
 */
@Composable
fun GlumeStatCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    badgeText: String? = null,
    badgeColor: Color = GlumePrimaryPurple,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.defaultMinSize(minHeight = 84.dp),
        shape = StatCardShape,
        color = GlumeSurfaceCard,
        border = BorderStroke(1.dp, GlumeBorder),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: Label & Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlumeTextSecondary,
                    maxLines = 1
                )
                Text(
                    text = icon,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxs))

            // Bottom row: Bold Value + Unit / Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )
                    if (unit != null) {
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                if (badgeText != null) {
                    Surface(
                        shape = PillShape,
                        color = badgeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Glume Circular Progress Ring Component (from reference screenshot):
 * Renders completion percentage / count inside an arc ring.
 */
@Composable
fun GlumeProgressRing(
    progressFraction: Float, // 0.0 to 1.0
    size: Dp = 68.dp,
    strokeWidth: Dp = 7.dp,
    ringColor: Color = GlumeSuccessMint,
    trackColor: Color = GlumeSurfaceElevated,
    modifier: Modifier = Modifier,
    centerContent: @Composable () -> Unit = {
        Text(
            text = "${(progressFraction * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = GlumeTextPrimary
        )
    }
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progressFraction.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        centerContent()
    }
}
