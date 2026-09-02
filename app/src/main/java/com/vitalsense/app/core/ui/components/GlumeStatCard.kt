package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.ui.util.touchSpring

/**
 * NagarSeva Stat & Metric Bento Grid Tile:
 * Elevated 2-column or 3-column Bento grid tile displaying large bold numbers (22sp bold),
 * icon badges, and small delta trend indicators (+12%, ↑, ↓) with tactile touch spring physics.
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
    trendText: String? = null,
    isTrendPositive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier
            .defaultMinSize(minHeight = 88.dp)
            .touchSpring(onClick = onClick),
        shape = StatCardShape,
        color = GlumeSurfaceCard,
        border = BorderStroke(1.dp, GlumeBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: Label & Icon Badge in Tinted Circle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = GlumeTextSecondary,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxs))

            // Bottom row: Bold Value (22sp) + Unit / Trend Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = GlumeTextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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

                // Delta Trend Indicator (+12%, ↑, ↓) or Badge
                val trend = trendText ?: badgeText
                if (trend != null) {
                    val trendBg = if (trendText != null) {
                        if (isTrendPositive) GlumeSuccessContainer else GlumeAlertContainer
                    } else {
                        badgeColor.copy(alpha = 0.15f)
                    }
                    val trendFg = if (trendText != null) {
                        if (isTrendPositive) GlumeSuccessMint else GlumeAlertCoral
                    } else {
                        badgeColor
                    }

                    Surface(
                        shape = PillShape,
                        color = trendBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            if (trendText != null) {
                                Text(
                                    text = if (isTrendPositive) "↑" else "↓",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = trendFg
                                )
                            }
                            Text(
                                text = trend,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = trendFg
                            )
                        }
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
