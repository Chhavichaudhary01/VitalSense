package com.vitalsense.app.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.data.model.UserRole
import com.vitalsense.app.core.network.ConnectivityState
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.R
import androidx.compose.ui.res.stringResource

@Composable
fun TopRoleSwitcherBar(
    currentRole: UserRole,
    activeUserName: String = "",
    activeProxyPatient: Patient? = null,
    onExitProxy: () -> Unit = {},
    isOffline: Boolean = false,
    connectivityState: ConnectivityState = ConnectivityState.ONLINE,
    isSyncing: Boolean = false,
    pendingOutboxCount: Int = 0,
    onManualSync: () -> Unit = {},
    onToggleOffline: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onToggleLanguage: () -> Unit = {},
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        ChangeLanguageDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { lang ->
                onSelectLanguage(lang)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VS_Background)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
    ) {
        // Floating Top Island Header (NagarSeva Design System)
        Surface(
            shape = PillShape,
            color = VS_Surface,
            border = BorderStroke(1.dp, VS_Outline),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = 6.dp),
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(VS_Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (currentRole) {
                                UserRole.PATIENT -> "🧑"
                                UserRole.ASHA -> "🩺"
                                UserRole.DOCTOR -> "👨‍⚕️"
                                UserRole.ADMIN -> "🛡️"
                            },
                            fontSize = 18.sp
                        )
                    }
                    Column {
                        Text(
                            text = if (activeUserName.isNotBlank()) activeUserName else stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VS_OnBackground,
                            maxLines = 1
                        )
                        Text(
                            text = when (currentRole) {
                                UserRole.PATIENT -> stringResource(R.string.patientPortal)
                                UserRole.ASHA -> stringResource(R.string.ashaPortal)
                                UserRole.DOCTOR -> stringResource(R.string.doctorPortal)
                                UserRole.ADMIN -> stringResource(R.string.adminPortal)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = VS_PrimaryContainer
                        )
                    }
                }

            // Right Actions: Sync Status, Connectivity Pill, Language & Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                // Prominent Network & Sync HUD Pill (§Presentation Wow Factor)
                val effectiveOffline = isOffline || connectivityState == ConnectivityState.OFFLINE

                val hudBgColor = when {
                    isSyncing -> VS_PrimaryContainer
                    effectiveOffline -> Color(0xFFFEF3C7) // Warm Amber
                    pendingOutboxCount > 0 -> Color(0xFFFEF3C7)
                    else -> Color(0xFFDCFCE7) // Crisp Mint
                }
                val hudBorderColor = when {
                    isSyncing -> VS_Primary
                    effectiveOffline -> Color(0xFFF59E0B)
                    pendingOutboxCount > 0 -> Color(0xFFF59E0B)
                    else -> Color(0xFF10B981)
                }
                val hudTextColor = when {
                    isSyncing -> VS_Primary
                    effectiveOffline -> Color(0xFFB45309)
                    pendingOutboxCount > 0 -> Color(0xFFB45309)
                    else -> Color(0xFF15803D)
                }

                Surface(
                    onClick = {
                        if (isOffline) {
                            onToggleOffline()
                        } else if (connectivityState == ConnectivityState.OFFLINE || pendingOutboxCount > 0) {
                            onManualSync()
                        } else {
                            onToggleOffline()
                        }
                    },
                    shape = PillShape,
                    color = hudBgColor,
                    border = BorderStroke(1.2.dp, hudBorderColor),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        when {
                            isSyncing -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = hudTextColor
                                )
                                Text(
                                    text = "Flushing Outbox to Cloud...",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = hudTextColor
                                )
                            }
                            effectiveOffline -> {
                                Text("🟡", fontSize = 10.sp)
                                val offlineLabel = when {
                                    isOffline -> "Demo Offline (Tap to Reconnect)"
                                    pendingOutboxCount > 0 -> "Offline: $pendingOutboxCount in Outbox"
                                    else -> "No Network / Airplane"
                                }
                                Text(
                                    text = offlineLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = hudTextColor
                                )
                            }
                            pendingOutboxCount > 0 -> {
                                Text("🔄", fontSize = 10.sp)
                                Text(
                                    text = "Flushing ($pendingOutboxCount in Outbox)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = hudTextColor
                                )
                            }
                            else -> {
                                Text("🟢", fontSize = 10.sp)
                                Text(
                                    text = "Cloud Synced",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = hudTextColor
                                )
                            }
                        }
                    }
                }

                // Global Language Switcher Pill
                Surface(
                    onClick = { showLanguageDialog = true },
                    shape = PillShape,
                    color = VS_Surface,
                    border = BorderStroke(1.dp, VS_Outline),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "🌐", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = currentLanguage.nativeName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = VS_OnBackground
                        )
                    }
                }

                // Logout / Exit Button
                Surface(
                    onClick = onLogout,
                    shape = PillShape,
                    color = VS_Surface,
                    border = BorderStroke(1.dp, VS_Outline),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "🚪", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = stringResource(R.string.exit),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = VS_OnBackground
                        )
                    }
                }
            }
        }
    }

        // Offline-First Room DB Banner (Audio 3)
        val effectiveOffline = isOffline || connectivityState == ConnectivityState.OFFLINE
        AnimatedVisibility(
            visible = effectiveOffline,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                color = Color(0xFFFFF7ED),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = 2.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFFDBA74))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚡", fontSize = 13.sp)
                        Column {
                            Text(
                                text = "OFFLINE-FIRST ENGINE ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFC2410C)
                                )
                            )
                            Text(
                                text = "Local Room DB instant writes (0ms) · Auto-syncs to cloud when signal returns",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = Color(0xFF9A3412)
                            )
                        }
                    }

                    Surface(
                        shape = PillShape,
                        color = Color(0xFFEA580C)
                    ) {
                        Text(
                            text = if (pendingOutboxCount > 0) "$pendingOutboxCount queued" else "Zero signal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Auto-Syncing In Progress Banner (Audio 3)
        AnimatedVisibility(
            visible = !effectiveOffline && (isSyncing || pendingOutboxCount > 0),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                color = VS_SuccessContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = 2.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, VS_Success.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = VS_Success
                        )
                        Text(
                            text = if (isSyncing) "Syncing queued records to cloud via WorkManager..." else "Signal restored: $pendingOutboxCount records syncing automatically",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VS_OnSuccessContainer
                            )
                        )
                    }
                    Text("✓ Cloud Synced", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = VS_Success))
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
                    color = VS_PrimaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xxs),
                    shape = CardShape,
                    border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.5f))
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
                                    text = stringResource(R.string.actingAsProxy),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VS_PrimaryContainer
                                )
                                Text(
                                    text = "${activeProxyPatient.name} (${activeProxyPatient.villageName})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = VS_OnBackground
                                )
                            }
                        }
                        Button(
                            onClick = onExitProxy,
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VS_Primary,
                                contentColor = VS_OnBackground
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = stringResource(R.string.exitProxy), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = VS_Outline
        )
    }
}
