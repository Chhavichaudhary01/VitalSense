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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.max
import kotlin.math.min

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

        item {
            VillageHeatMap(villages = villages)
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

@Composable
fun VillageHeatMap(villages: List<Village>, modifier: Modifier = Modifier) {
    if (villages.isEmpty()) return

    // Find min and max bounds to normalize coordinates
    val minLat = villages.minOf { it.latitude }
    val maxLat = villages.maxOf { it.latitude }
    val minLng = villages.minOf { it.longitude }
    val maxLng = villages.maxOf { it.longitude }

    val latRange = max(maxLat - minLat, 0.01) // Prevent division by zero
    val lngRange = max(maxLng - minLng, 0.01)

    VitalSenseCard(
        elevation = 4.dp,
        backgroundColor = DarkCharcoal,
        modifier = modifier.fillMaxWidth().height(300.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw a simple grid background
                val gridStep = 50f
                for (x in 0..canvasWidth.toInt() step gridStep.toInt()) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), canvasHeight),
                        strokeWidth = 1f
                    )
                }
                for (y in 0..canvasHeight.toInt() step gridStep.toInt()) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(canvasWidth, y.toFloat()),
                        strokeWidth = 1f
                    )
                }

                // Plot each village
                villages.forEach { village ->
                    // Normalize X and Y (Longitude maps to X, Latitude maps to Y, invert Y so higher lat is 'up')
                    val normX = ((village.longitude - minLng) / lngRange).toFloat()
                    val normY = 1f - ((village.latitude - minLat) / latRange).toFloat()

                    // Padding to keep nodes off the exact edge
                    val padding = 40f
                    val x = padding + normX * (canvasWidth - padding * 2)
                    val y = padding + normY * (canvasHeight - padding * 2)

                    val isHighOutbreak = village.highRiskCount >= 3
                    val baseColor = if (isHighOutbreak) CoralAlert else AmberWarning
                    
                    // Base size based on active cases
                    val radius = 20f + (village.activeCases * 1.5f).coerceAtMost(60f)

                    // Draw glowing heat aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(baseColor.copy(alpha = 0.6f), Color.Transparent),
                            center = Offset(x, y),
                            radius = radius * 2
                        ),
                        radius = radius * 2,
                        center = Offset(x, y)
                    )

                    // Draw solid core
                    drawCircle(
                        color = baseColor,
                        radius = 8f,
                        center = Offset(x, y)
                    )
                }
            }

            // Overlay textual labels for the villages
            villages.forEach { village ->
                val normX = ((village.longitude - minLng) / lngRange).toFloat()
                val normY = 1f - ((village.latitude - minLat) / latRange).toFloat()

                // Calculate approx dp position manually to position a Compose Text element
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp) // match padding
                ) {
                    // We use an alignment trick or absolute offset to position text. 
                    // Since it's tricky to mix Canvas and Compose perfectly without exact Layout, 
                    // we can just use Box align for a cool HUD look, or list them at the bottom.
                }
            }
            
            // For simplicity and guaranteed perfect rendering, we will list the legend overlaid.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CardShape)
                    .padding(8.dp)
            ) {
                Text("🔴 High Risk Outbreak", color = CoralAlert, style = MaterialTheme.typography.labelSmall)
                Text("🟡 Monitored Zone", color = AmberWarning, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
