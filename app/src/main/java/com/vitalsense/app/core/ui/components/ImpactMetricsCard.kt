package com.vitalsense.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.ui.theme.*

/**
 * Quantified Bharat Healthcare Impact Card
 * Directly showcases the metrics highlighted in Audio 10:
 * - OPD wait time cut: 75 min -> < 5 min
 * - Travel & Cost saved: 30-70 km & up to ₹500 per visit
 * - Specialist referral turnaround: 7-14 days -> < 24 hours
 * - SIH26133 & Team AarogyaX attribution
 */
@Composable
fun ImpactMetricsCard(
    modifier: Modifier = Modifier,
    collapsible: Boolean = true,
    initiallyExpanded: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VS_Surface),
        border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (collapsible) Modifier.clickable { isExpanded = !isExpanded } else Modifier),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VS_PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🇮🇳", fontSize = 18.sp)
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Bharat Healthcare Impact",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VS_OnBackground
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = VS_PrimaryContainer
                            ) {
                                Text(
                                    text = "SIH26133",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VS_Primary
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Team AarogyaX · Measurable Last-Mile Outcomes",
                            style = MaterialTheme.typography.bodySmall,
                            color = VS_OnSurfaceVariant
                        )
                    }
                }

                if (collapsible) {
                    Text(
                        text = if (isExpanded) "▲" else "▼",
                        color = VS_OnSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = VS_Outline.copy(alpha = 0.5f))

                    // 3-Column Quantified Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Metric 1: OPD Waiting Time
                        ImpactMetricItem(
                            modifier = Modifier.weight(1f),
                            icon = "⏱️",
                            title = "OPD Wait",
                            baseline = "75 min",
                            outcome = "< 5 min",
                            badge = "-93%",
                            badgeColor = VS_Success
                        )

                        // Metric 2: Travel & Financial Saved
                        ImpactMetricItem(
                            modifier = Modifier.weight(1f),
                            icon = "💰",
                            title = "Saved / Visit",
                            baseline = "30–70 km",
                            outcome = "₹500 saved",
                            badge = "Direct",
                            badgeColor = VS_Primary
                        )

                        // Metric 3: Specialist Referral Loop
                        ImpactMetricItem(
                            modifier = Modifier.weight(1f),
                            icon = "🔄",
                            title = "Referrals",
                            baseline = "7–14 days",
                            outcome = "< 24 hrs",
                            badge = "Closed Loop",
                            badgeColor = VS_Secondary
                        )
                    }

                    // Key Architectural Enabler Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VS_SurfaceVariant,
                        border = BorderStroke(1.dp, VS_Outline.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚡", fontSize = 14.sp)
                            Text(
                                text = "Zero-signal Room DB local writes (0ms) · Automatic cloud sync on reconnection",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = VS_OnBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImpactMetricItem(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    baseline: String,
    outcome: String,
    badge: String,
    badgeColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = VS_SurfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, VS_Outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(icon, fontSize = 13.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = VS_OnSurfaceVariant,
                    maxLines = 1
                )
            }

            Text(
                text = outcome,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                ),
                color = VS_OnBackground
            )

            Text(
                text = "Was: $baseline",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = VS_OnSurfaceVariant
                )
            )

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = badgeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
