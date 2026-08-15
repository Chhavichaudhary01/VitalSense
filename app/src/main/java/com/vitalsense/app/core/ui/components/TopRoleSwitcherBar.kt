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
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onToggleLanguage: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GlumeBackground)
    ) {
        // Main App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo & Role Scoped User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(GlumeSurfaceElevated),
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
                        text = if (activeUserName.isNotBlank()) activeUserName else strings.appName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = when (currentRole) {
                            UserRole.PATIENT -> strings.patientPortal
                            UserRole.ASHA -> strings.ashaPortal
                            UserRole.DOCTOR -> strings.doctorPortal
                            UserRole.ADMIN -> strings.adminPortal
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumeTextSecondary
                    )
                }
            }

            // Right Actions: Language Toggle, Connectivity Pill & Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Global Language Switcher Pill
                Surface(
                    onClick = onToggleLanguage,
                    shape = PillShape,
                    color = GlumeSurfaceCard,
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "🌐", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "हिंदी" else "EN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                    }
                }

                // Connectivity Mode Pill
                Surface(
                    onClick = onToggleOffline,
                    shape = PillShape,
                    color = if (isOffline) GlumeSurfaceElevated else GlumeSuccessContainer,
                    border = BorderStroke(1.dp, if (isOffline) GlumeBorder else GlumeSuccessMint.copy(alpha = 0.4f)),
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
                                .background(if (isOffline) GlumeTextTertiary else GlumeSuccessMint)
                        )
                        Text(
                            text = if (isOffline) strings.offline else strings.online,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isOffline) GlumeTextSecondary else GlumeSuccessText
                        )
                    }
                }

                // Logout / Exit Button
                Surface(
                    onClick = onLogout,
                    shape = PillShape,
                    color = GlumeSurfaceCard,
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "🚪", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = strings.exit,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
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
                    color = GlumePrimaryPurpleContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xxs),
                    shape = CardShape,
                    border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.5f))
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
                                    text = strings.actingAsProxy,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumePrimaryPurpleLight
                                )
                                Text(
                                    text = "${activeProxyPatient.name} (${activeProxyPatient.villageName})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = GlumeTextPrimary
                                )
                            }
                        }
                        Button(
                            onClick = onExitProxy,
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GlumePrimaryPurple,
                                contentColor = GlumeTextPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = strings.exitProxy, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = GlumeBorderSubtle
        )
    }
}
