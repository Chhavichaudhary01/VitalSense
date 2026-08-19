package com.vitalsense.app.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.feature.admin.components.DistrictOutbreakMapView
import kotlin.math.max

@Composable
fun AdminHomeScreen(
    villages: List<Village>,
    notices: List<BroadcastNotice>,
    dispensaryStock: List<DispensaryItem> = emptyList(),
    onSendBroadcast: (title: String, message: String, village: String?) -> Unit,
    onNavigateToDispensary: () -> Unit,
    onNavigateToDiseaseTrends: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var selectedVillageName by remember { mutableStateOf("All Villages") }
    var selectedMapVillage by remember { mutableStateOf<Village?>(villages.firstOrNull()) }
    var isFormError by remember { mutableStateOf(false) }

    val adminIssuedDirectives = notices.filter { it.senderRole == UserRole.ADMIN }
    val totalActiveCases = villages.sumOf { it.activeCases }
    val totalPopulation = villages.sumOf { it.population }
    val outbreakCount = villages.count { it.highRiskCount > 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlumeBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Admin Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = strings.districtCommand,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = GlumeTextPrimary
                )
                Text(
                    text = "Surveillance Region: Rampur District, UP (Pop: $totalPopulation)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GlumeTextSecondary
                )
            }
        }

        // 2. Summary stats (Glume 3-Column Compact Grid)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                GlumeStatCard(
                    label = strings.totalActiveCases,
                    value = "$totalActiveCases",
                    icon = "🚨",
                    modifier = Modifier.weight(1f),
                    badgeText = if (totalActiveCases > 0) "Active" else null,
                    badgeColor = GlumeAlertCoral
                )
                GlumeStatCard(
                    label = "Monitored",
                    value = "${villages.size}",
                    icon = "🏡",
                    modifier = Modifier.weight(1f),
                    badgeText = "Villages",
                    badgeColor = GlumePrimaryPurple
                )
                GlumeStatCard(
                    label = "Outbreaks",
                    value = "$outbreakCount",
                    icon = "⚠️",
                    modifier = Modifier.weight(1f),
                    badgeText = if (outbreakCount > 0) "Clusters" else "Safe",
                    badgeColor = if (outbreakCount > 0) GlumeAlertCoral else GlumeSuccessMint
                )
            }
        }

        // 3. Section: Village Disease Trend Heat Map Cards
        item {
            Text(
                text = "🗺️ Google Outbreak Surveillance Map",
                style = MaterialTheme.typography.headlineMedium,
                color = GlumeTextPrimary
            )
        }

        // 3.1 Google Maps Outbreak Surveillance View
        item {
            DistrictOutbreakMapView(
                villages = villages,
                selectedVillage = selectedMapVillage,
                onSelectVillage = { selectedMapVillage = it },
                onBroadcastToVillage = { village ->
                    selectedVillageName = village.name
                    broadcastTitle = "Health Advisory for ${village.name}"
                    broadcastMessage = "Urgent: Heightened medical monitoring active for ${village.name}. Please consult your nearest ASHA worker."
                    showBroadcastDialog = true
                }
            )
        }

        // 3.2 Monitored Village Cards List
        item {
            Text(
                text = "Village Health Telemetry (${villages.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = GlumeTextPrimary
            )
        }

        items(villages) { village ->
            val isHighRisk = village.highRiskCount > 0
            val isSelected = selectedMapVillage?.id == village.id
            val riskLevel = if (village.highRiskCount > 2) SeverityLevel.SEVERE else if (village.highRiskCount > 0) SeverityLevel.HIGH else if (village.activeCases > 5) SeverityLevel.MODERATE else SeverityLevel.LOW

            VitalSenseCard(
                backgroundColor = if (isSelected) GlumeSurfaceElevated else if (isHighRisk) GlumeAlertContainer else GlumeSurfaceCard,
                border = BorderStroke(1.dp, if (isSelected) GlumePrimaryPurple else if (isHighRisk) GlumeAlertCoral.copy(alpha = 0.4f) else GlumeBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMapVillage = village },
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Text(
                                    text = village.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                if (isSelected) {
                                    Surface(shape = PillShape, color = GlumePrimaryPurple) {
                                        Text(
                                            text = "PINNED ON MAP 📍",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Population: ${village.population} · Active Cases: ${village.activeCases} · High Risk: ${village.highRiskCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
                        }
                        SeverityBadge(severity = riskLevel)
                    }

                    // Progress Bar
                    val ratio = (village.activeCases.toFloat() / max(village.population, 1) * 100f).coerceIn(0f, 100f)
                    LinearProgressIndicator(
                        progress = { (ratio / 10f).coerceIn(0.05f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(PillShape),
                        color = when (riskLevel) {
                            SeverityLevel.SEVERE -> GlumeAlertCoral
                            SeverityLevel.HIGH -> GlumeAlertCoral
                            SeverityLevel.MODERATE -> GlumeWarningAmber
                            SeverityLevel.LOW -> GlumeSuccessMint
                        },
                        trackColor = GlumeSurfaceElevated,
                    )
                }
            }
        }

        // 4. Admin Management Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                VitalSenseButton(
                    text = "Manage Dispensary",
                    onClick = onNavigateToDispensary,
                    style = ButtonStyle.SECONDARY,
                    modifier = Modifier.weight(1f)
                )
                VitalSenseButton(
                    text = "Disease Trends",
                    onClick = onNavigateToDiseaseTrends,
                    style = ButtonStyle.SECONDARY,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Broadcast Action Button (Single Full-Width Purple CTA)
        item {
            VitalSenseButton(
                text = "📢 Broadcast District-Wide Health Directive",
                onClick = {
                    selectedVillageName = "All Villages"
                    showBroadcastDialog = true
                },
                style = ButtonStyle.PRIMARY
            )
        }

        // 6. Active Directives Sent by Admin
        if (adminIssuedDirectives.isNotEmpty()) {
            item {
                Text(
                    text = "Dispatched Health Directives (${adminIssuedDirectives.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GlumeTextPrimary
                )
            }

            items(adminIssuedDirectives) { directive ->
                VitalSenseCard {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = directive.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Surface(shape = PillShape, color = GlumeSuccessContainer) {
                                Text(
                                    text = "DISPATCHED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GlumeSuccessText
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = directive.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Target: ${directive.targetVillage ?: "All Villages"} · Sender: ${directive.senderName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                }
            }
        }

        // 7. District Dispensary Stock Check (Summary)
        if (dispensaryStock.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dispensary Low Stock Alerts",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GlumeTextPrimary
                    )
                }
            }

            val lowStockItems = dispensaryStock.filter { it.isLowStock }
            if (lowStockItems.isNotEmpty()) {
                items(lowStockItems) { item ->
                    VitalSenseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.medicineName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Text(
                                    text = "${item.availableQuantity} ${item.unit}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GlumeAlertCoral
                                    )
                                )
                                Surface(shape = PillShape, color = GlumeAlertContainer) {
                                    Text(
                                        text = "LOW",
                                        style = MaterialTheme.typography.labelSmall.copy(color = GlumeAlertCoral, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("All stock is above reorder thresholds.", color = GlumeTextSecondary)
                }
            }
        }
    }

    // Broadcast Notice Modal Dialog
    if (showBroadcastDialog) {
        VitalSenseDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = "Broadcast Health Directive",
            subtitle = "Push real-time alert to Doctors, ASHA workers & Patients",
            icon = { Text("📢", fontSize = 22.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isBlank() || broadcastMessage.isBlank()) {
                            isFormError = true
                        } else {
                            val villageParam = if (selectedVillageName == "All Villages") null else selectedVillageName
                            onSendBroadcast(broadcastTitle.trim(), broadcastMessage.trim(), villageParam)
                            broadcastTitle = ""
                            broadcastMessage = ""
                            showBroadcastDialog = false
                            isFormError = false
                        }
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple)
                ) {
                    Text("Broadcast Now", color = GlumeTextPrimary, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBroadcastDialog = false },
                    shape = PillShape
                ) {
                    Text(strings.cancel, color = GlumeTextSecondary, style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                VitalSenseTextField(
                    value = broadcastTitle,
                    onValueChange = { broadcastTitle = it },
                    label = "Directive Title",
                    placeholder = "e.g. Water Contamination Boil Notice",
                    isError = isFormError && broadcastTitle.isBlank(),
                    errorMessage = "Title is required"
                )

                VitalSenseTextField(
                    value = broadcastMessage,
                    onValueChange = { broadcastMessage = it },
                    label = "Directive Message",
                    placeholder = "Detailed guidance, precautionary steps, and dispatch protocols...",
                    singleLine = false,
                    maxLines = 4,
                    isError = isFormError && broadcastMessage.isBlank(),
                    errorMessage = "Message is required"
                )

                Text(
                    text = "Target Village / Audience",
                    style = MaterialTheme.typography.labelSmall,
                    color = GlumeTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    val villageOptions = listOf("All Villages") + villages.map { it.name }
                    villageOptions.forEach { vName ->
                        val isSelected = selectedVillageName == vName
                        Surface(
                            onClick = { selectedVillageName = vName },
                            shape = PillShape,
                            color = if (isSelected) GlumePrimaryPurpleContainer else GlumeSurfaceCard,
                            border = if (isSelected) BorderStroke(1.5.dp, GlumePrimaryPurple) else BorderStroke(1.dp, GlumeBorder)
                        ) {
                            Text(
                                text = vName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GlumePrimaryPurpleLight else GlumeTextPrimary
                                ),
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                            )
                        }
                    }
                }
            }
        }
    }
}
