package com.vitalsense.app.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.data.model.UserRole
import com.vitalsense.app.core.ui.theme.*

@Composable
fun TopRoleSwitcherBar(
    currentRole: UserRole,
    activeUserName: String = "",
    activeProxyPatient: Patient? = null,
    onExitProxy: () -> Unit = {},
    isOffline: Boolean = false,
    onToggleOffline: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(WarmCreamBackground)
    ) {
        // Main App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo & Role Scoped User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (currentRole) {
                                UserRole.PATIENT -> LimePrimary
                                UserRole.ASHA -> LavenderSecondary
                                UserRole.DOCTOR -> BlushPinkTertiary
                                UserRole.ADMIN -> AmberWarning
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (currentRole) {
                            UserRole.PATIENT -> "👤"
                            UserRole.ASHA -> "🤝"
                            UserRole.DOCTOR -> "🩺"
                            UserRole.ADMIN -> "🛡️"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = if (activeUserName.isNotBlank()) activeUserName else "VitalSense",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryNearBlack,
                        maxLines = 1
                    )
                    Text(
                        text = when (currentRole) {
                            UserRole.PATIENT -> "Patient Portal"
                            UserRole.ASHA -> "ASHA Worker Caseload"
                            UserRole.DOCTOR -> "Clinical Review Portal"
                            UserRole.ADMIN -> "District Outbreak Command"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                }
            }

            // Right Actions: Connectivity Pill & Logout (Enforcing accessible 48dp touch bounds)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Connectivity Mode Pill
                Surface(
                    onClick = onToggleOffline,
                    shape = PillShape,
                    color = if (isOffline) Color(0xFFF3EFE8) else SoftMintSuccess.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, CardBorderSubtle),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color.Gray else SoftMintText)
                        )
                        Text(
                            text = if (isOffline) "Offline" else "Online",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isOffline) TextSecondaryMuted else SoftMintText
                        )
                    }
                }

                // Logout / Exit Button
                Surface(
                    onClick = onLogout,
                    shape = PillShape,
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 1.dp,
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "🚪", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "Exit",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                    }
                }
            }
        }

        // ASHA Proxy Indicator Banner
        AnimatedVisibility(
            visible = activeProxyPatient != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            if (activeProxyPatient != null) {
                Surface(
                    color = AmberWarning.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xxs),
                    shape = CardShape,
                    border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🤝", style = MaterialTheme.typography.titleMedium)
                            Column {
                                Text(
                                    text = "Acting as Proxy for Patient:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryNearBlack
                                )
                                Text(
                                    text = "${activeProxyPatient.name} (${activeProxyPatient.villageName})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimaryNearBlack
                                )
                            }
                        }
                        Button(
                            onClick = onExitProxy,
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkCharcoal,
                                contentColor = LimePrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = "Exit Proxy", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = DividerSubtle
        )
    }
}
