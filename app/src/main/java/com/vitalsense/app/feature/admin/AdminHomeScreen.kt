package com.vitalsense.app.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*

@Composable
fun AdminHomeScreen(
    villages: List<Village>,
    notices: List<BroadcastNotice>,
    dispensaryStock: List<DispensaryItem> = emptyList(),
    onSendBroadcast: (title: String, message: String, village: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var selectedVillageName by remember { mutableStateOf("All Villages") }
    var isFormError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // 1. Admin Header
        item {
            Column {
                Text(
                    text = "District Health Command",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "Surveillance Region: Rampur District, UP",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 2. Summary stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = CoralAlert.copy(alpha = 0.2f)
                ) {
                    Column {
                        Text(
                            text = "${villages.sumOf { it.activeCases }}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CoralAlert
                            )
                        )
                        Text(text = "Total Active Cases", style = MaterialTheme.typography.labelSmall)
                    }
                }

                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = LimePrimary.copy(alpha = 0.6f)
                ) {
                    Column {
                        Text(
                            text = "${villages.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(text = "Monitored Villages", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // 3. Section: Village Disease Trend Heat Map Cards (PRD §4.2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🗺️ Village Outbreak Surveillance",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "Live Trend Refresh",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
        }

        items(villages) { village ->
            val isHighOutbreak = village.highRiskCount >= 3
            VitalSenseCard(
                elevation = 2.dp,
                backgroundColor = if (isHighOutbreak) CoralAlert.copy(alpha = 0.12f) else SurfaceWhite
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Village ${village.name}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Population: ${village.population} · Coordinates: ${village.latitude}, ${village.longitude}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = if (isHighOutbreak) CoralAlert else SoftMintSuccess.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = if (isHighOutbreak) "⚠️ OUTBREAK ALERT" else "● Normal",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHighOutbreak) SurfaceWhite else Color(0xFF1B5E20)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Active Cases: ${village.activeCases}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Severe Clusters: ${village.highRiskCount}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isHighOutbreak) CoralAlert else TextPrimaryNearBlack
                            )
                        )
                    }
                }
            }
        }

        // 4. Quick Action: Outbreak Broadcast
        item {
            VitalSenseButton(
                text = "📢 Broadcast Health Directive / Alert",
                onClick = { showBroadcastDialog = true },
                style = ButtonStyle.DARK
            )
        }

        // 4.5 District Dispensary Inventory (PRD Mock Requirement)
        item {
            Text(
                text = "🏥 District Dispensary Inventory",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimaryNearBlack
            )
        }

        items(dispensaryStock) { item ->
            val isLowStock = item.availableQuantity <= item.reorderThreshold
            VitalSenseCard(
                backgroundColor = if (isLowStock) CoralAlert.copy(alpha = 0.1f) else SurfaceWhite
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.medicineName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Category: ${item.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${item.availableQuantity} ${item.unit}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isLowStock) CoralAlert else TextPrimaryNearBlack
                            )
                        )
                        if (isLowStock) {
                            Text(
                                text = "LOW STOCK",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CoralAlert
                            )
                        }
                    }
                }
            }
        }

        // 5. Active Directives Sent
        item {
            Text(
                text = "Active Directives & Broadcasts",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimaryNearBlack
            )
        }

        items(notices) { notice ->
            VitalSenseCard {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = notice.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = notice.message,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Target: ${notice.targetRole} (${notice.targetVillage ?: "All Villages"}) · Sender: ${notice.senderName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                }
            }
        }
    }

    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false; isFormError = false },
            title = { Text("📢 Send Outbreak Directive", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it; isFormError = false },
                        label = { Text("Directive Title") },
                        placeholder = { Text("e.g. ⚠️ Dengue Outbreak Alert") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isFormError && broadcastTitle.isBlank()
                    )
                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it; isFormError = false },
                        label = { Text("Directive Details / Action Instructions") },
                        placeholder = { Text("Conduct door-to-door testing...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        isError = isFormError && broadcastMessage.isBlank()
                    )
                    if (isFormError) {
                        Text(
                            text = "Title and message cannot be empty",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                            onSendBroadcast(broadcastTitle.trim(), broadcastMessage.trim(), null)
                            broadcastTitle = ""
                            broadcastMessage = ""
                            isFormError = false
                            showBroadcastDialog = false
                        } else {
                            isFormError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal)
                ) {
                    Text("Broadcast Now", color = LimePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false; isFormError = false }) {
                    Text("Cancel", color = TextPrimaryNearBlack)
                }
            }
        )
    }
}
