package com.vitalsense.app.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo & Role Scoped User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
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
                        fontSize = 18.sp
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (activeUserName.isNotBlank()) activeUserName else "VitalSense",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextPrimaryNearBlack
                        )
                    }
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

            // Right Actions: Connectivity Pill & Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Connectivity Mode Pill
                Surface(
                    shape = PillShape,
                    color = if (isOffline) Color(0xFFF0EDE6) else SoftMintSuccess.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { onToggleOffline() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color.Gray else Color(0xFF2E7D32))
                        )
                        Text(
                            text = if (isOffline) "Offline" else "Online",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isOffline) TextSecondaryMuted else Color(0xFF1B5E20)
                        )
                    }
                }

                // Logout / Exit Button
                Surface(
                    shape = PillShape,
                    color = SurfaceWhite,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { onLogout() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(text = "🚪", fontSize = 11.sp)
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                    }
                }
            }
        }

        // ASHA Proxy Indicator Banner (Only shown when ASHA is acting as proxy for a patient)
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
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = CardShape
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🤝", fontSize = 14.sp)
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
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(text = "Exit Proxy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFF0EAE0),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
