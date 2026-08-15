package com.vitalsense.app.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*
import kotlin.math.max

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

    val adminIssuedDirectives = notices.filter { it.senderRole == UserRole.ADMIN }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Admin Header
        item {
            Column {
                Text(
                    text = "District Health Command",
                    style = MaterialTheme.typography.displayMedium,
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
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = CoralAlert.copy(alpha = 0.15f),
                    contentPadding = Spacing.sm
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "${villages.sumOf { it.activeCases }}",
                            style = MaterialTheme.typography.displayMedium.copy(color = CoralAlertDark)
                        )
                        Text(text = "Total Active Cases", style = MaterialTheme.typography.labelSmall)
                    }
                }

                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = LimePrimary.copy(alpha = 0.6f),
                    contentPadding = Spacing.sm
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "${villages.size}",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(text = "Monitored Villages", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // 3. Section: Village Disease Trend Heat Map Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🗺️ Outbreak Surveillance",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "Live Telemetry Refresh",
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

        // 4.1 Admin Issued Directives Log
        if (adminIssuedDirectives.isNotEmpty()) {
            item {
                Text(
                    text = "📋 Dispatched Directives (${adminIssuedDirectives.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
            }

            items(adminIssuedDirectives) { directive ->
                VitalSenseCard(backgroundColor = SurfaceWhite) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = directive.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimaryNearBlack
                            )
                            Surface(shape = PillShape, color = SoftMintSuccess.copy(alpha = 0.5f)) {
                                Text(
                                    text = "ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SoftMintText),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                )
                            }
                        }
                        Text(
                            text = directive.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Target: ${directive.targetVillage ?: "All Monitored Villages"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 4.5 District Dispensary Inventory
        item {
            Text(
                text = "🏥 District Dispensary Inventory",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimaryNearBlack
            )
        }

        items(dispensaryStock) { item ->
            val isLowStock = item.availableQuantity <= item.reorderThreshold
            VitalSenseCard(
                backgroundColor = if (isLowStock) CoralAlert.copy(alpha = 0.08f) else SurfaceWhite
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.medicineName,
                            style = MaterialTheme.typography.titleMedium
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
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isLowStock) CoralAlertDark else TextPrimaryNearBlack
                            )
                        )
                        if (isLowStock) {
                            Text(
                                text = "LOW STOCK",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CoralAlertDark
                            )
                        }
                    }
                }
            }
        }

        // 5. Monitored Villages breakdown
        item {
            Text(
                text = "Monitored Villages (${villages.size})",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimaryNearBlack
            )
        }

        items(villages) { village ->
            val hasHighRisk = village.highRiskCount > 0
            VitalSenseCard(
                backgroundColor = if (hasHighRisk) CoralAlert.copy(alpha = 0.08f) else SurfaceWhite
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = village.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Population: ${village.population} · Active Cases: ${village.activeCases}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }

                        if (hasHighRisk) {
                            Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.2f)) {
                                Text(
                                    text = "${village.highRiskCount} HIGH RISK",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CoralAlertDark, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                )
                            }
                        }
                    }

                    val villageRiskLevel = when {
                        village.highRiskCount > 0 -> SeverityLevel.SEVERE
                        village.activeCases >= 3 -> SeverityLevel.HIGH
                        village.activeCases > 0 -> SeverityLevel.MODERATE
                        else -> SeverityLevel.LOW
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "State: ${village.state} (${village.district})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Risk: ${villageRiskLevel.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (villageRiskLevel) {
                                    SeverityLevel.SEVERE -> CoralAlertDark
                                    SeverityLevel.HIGH -> OrangeHighRisk
                                    SeverityLevel.MODERATE -> AmberWarningDark
                                    SeverityLevel.LOW -> SoftMintText
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    // Broadcast Notice Modal
    if (showBroadcastDialog) {
        VitalSenseDialog(
            onDismissRequest = { showBroadcastDialog = false; isFormError = false },
            title = "📢 Broadcast Health Directive",
            subtitle = "Directly dispatches notification to Patients, ASHA workers, and Doctors",
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                            onSendBroadcast(
                                broadcastTitle.trim(),
                                broadcastMessage.trim(),
                                if (selectedVillageName == "All Villages") null else selectedVillageName
                            )
                            broadcastTitle = ""
                            broadcastMessage = ""
                            selectedVillageName = "All Villages"
                            isFormError = false
                            showBroadcastDialog = false
                        } else {
                            isFormError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text("Broadcast Now", color = LimePrimary, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBroadcastDialog = false; isFormError = false },
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                VitalSenseTextField(
                    value = broadcastTitle,
                    onValueChange = { broadcastTitle = it },
                    label = "Directive Title",
                    placeholder = "e.g. Seasonal Dengue / Malaria Advisory",
                    isError = isFormError && broadcastTitle.isBlank(),
                    errorMessage = "Title cannot be empty"
                )

                VitalSenseTextField(
                    value = broadcastMessage,
                    onValueChange = { broadcastMessage = it },
                    label = "Detailed Message & Guidelines",
                    placeholder = "Boil drinking water, use mosquito nets, report fevers...",
                    singleLine = false,
                    maxLines = 4,
                    isError = isFormError && broadcastMessage.isBlank(),
                    errorMessage = "Message cannot be empty"
                )

                Text(
                    text = "Target Scope:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )

                val villageOptions = listOf("All Villages") + villages.map { it.name }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    villageOptions.take(3).forEach { option ->
                        val isSelected = selectedVillageName == option
                        Surface(
                            onClick = { selectedVillageName = option },
                            shape = PillShape,
                            color = if (isSelected) LimePrimary else SurfaceWhite,
                            border = BorderStroke(1.dp, if (isSelected) DarkCharcoal else CardBorderColor)
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                                color = TextPrimaryNearBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VillageHeatMap(villages: List<Village>) {
    VitalSenseCard(
        backgroundColor = DarkCharcoal,
        elevation = 3.dp,
        border = BorderStroke(1.dp, Color(0xFF333528))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Geo-Density Outbreak Mapping",
                    color = SurfaceWhite,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "GPS Coordinates HUD",
                    color = TextSecondaryMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1E1E1E), shape = CardShape)
            ) {
                if (villages.isEmpty()) {
                    Text(
                        text = "No village geolocation telemetry",
                        color = TextSecondaryMuted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    val minLat = villages.minOf { it.latitude }
                    val maxLat = villages.maxOf { it.latitude }
                    val minLng = villages.minOf { it.longitude }
                    val maxLng = villages.maxOf { it.longitude }

                    val latRange = max(maxLat - minLat, 0.001)
                    val lngRange = max(maxLng - minLng, 0.001)

                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.md)
                    ) {
                        val width = size.width
                        val height = size.height

                        villages.forEach { village ->
                            val normX = ((village.longitude - minLng) / lngRange).toFloat()
                            val normY = 1f - ((village.latitude - minLat) / latRange).toFloat()

                            val x = normX * width
                            val y = normY * height

                            val radius = (15f + (village.activeCases * 8f)).coerceIn(15f, 45f)
                            val baseColor = when {
                                village.highRiskCount > 0 || village.activeCases >= 3 -> CoralAlert
                                village.activeCases > 0 -> AmberWarning
                                else -> SoftMintSuccess
                            }

                            // Draw gradient pulse aura
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(baseColor.copy(alpha = 0.6f), Color.Transparent),
                                    center = Offset(x, y),
                                    radius = radius * 2
                                ),
                                radius = radius * 2,
                                center = Offset(x, y)
                            )

                            // Draw solid center node
                            drawCircle(
                                color = baseColor,
                                radius = 7f,
                                center = Offset(x, y)
                            )
                        }
                    }

                    // HUD Overlay Legend
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(Spacing.xs)
                            .background(Color.Black.copy(alpha = 0.6f), shape = CardShape)
                            .padding(Spacing.xxs)
                    ) {
                        Text("🔴 High Risk / Cluster", color = CoralAlert, style = MaterialTheme.typography.labelSmall)
                        Text("🟡 Monitored Cases", color = AmberWarning, style = MaterialTheme.typography.labelSmall)
                        Text("🟢 Low / Stable", color = SoftMintSuccess, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
