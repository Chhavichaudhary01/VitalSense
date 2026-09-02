package com.vitalsense.app.feature.biomedical

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.BioMedicalEquipment
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseTextField
import com.vitalsense.app.core.ui.theme.*

@Composable
fun BioMedicalScreen(
    equipmentList: List<BioMedicalEquipment>,
    onBackClick: () -> Unit,
    onUpdateEquipment: (BioMedicalEquipment) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedEquipmentForMaint by remember { mutableStateOf<BioMedicalEquipment?>(null) }

    val filterOptions = listOf("ALL", "OPERATIONAL", "CALIBRATION_DUE", "UNDER_MAINTENANCE")

    val filteredList = remember(equipmentList, selectedFilter) {
        if (selectedFilter == "ALL") equipmentList else equipmentList.filter { it.status == selectedFilter }
    }

    val operationalCount = equipmentList.count { it.status == "OPERATIONAL" }
    val attentionCount = equipmentList.count { it.status != "OPERATIONAL" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlumeBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.md, bottom = Spacing.xxl)
    ) {
        // 1. Header with Back Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = PillShape,
                    color = GlumeSurfaceCard,
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text("←", color = GlumeTextPrimary, fontWeight = FontWeight.Bold)
                        Text("Hospital Desk", style = MaterialTheme.typography.labelMedium, color = GlumeTextPrimary)
                    }
                }

                Surface(
                    shape = PillShape,
                    color = GlumePrimaryPurpleContainer,
                    border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Hospital Care · BME",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumePrimaryPurpleLight,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                    )
                }
            }
        }

        // 2. Hero Bio-Medical HUD
        item {
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                border = BorderStroke(1.dp, GlumeBorder)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "⚡ Bio-Medical Equipment Registry",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "Critical Medical Infrastructure & Maintenance Health",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = GlumeSuccessContainer
                        ) {
                            Text(
                                text = "$operationalCount / ${equipmentList.size} Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumeSuccessMint,
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = GlumeBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Fully Operational", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text("$operationalCount Units", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GlumeSuccessMint)
                        }
                        Column {
                            Text("Maintenance / Due", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text("$attentionCount Units", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = if (attentionCount > 0) GlumeAlertCoral else GlumeSuccessMint)
                        }
                        Column {
                            Text("BME Engineering", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text("24x7 On-Call", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GlumePrimaryPurpleLight)
                        }
                    }
                }
            }
        }

        // 3. Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        onClick = { selectedFilter = filter },
                        shape = PillShape,
                        color = if (isSelected) GlumePrimaryPurple else GlumeSurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) GlumePrimaryPurpleVariant else GlumeBorder)
                    ) {
                        Text(
                            text = filter.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) GlumeTextPrimary else GlumeTextSecondary,
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                        )
                    }
                }
            }
        }

        // 4. Equipment Cards
        items(filteredList, key = { it.id }) { equip ->
            val isOperational = equip.status == "OPERATIONAL"
            val isCalibrationDue = equip.status == "CALIBRATION_DUE"

            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                border = BorderStroke(
                    1.dp,
                    when {
                        isOperational -> GlumeBorder
                        isCalibrationDue -> GlumeWarningAmber.copy(alpha = 0.5f)
                        else -> GlumeAlertCoral.copy(alpha = 0.5f)
                    }
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = PillShape,
                            color = GlumeSurfaceElevated
                        ) {
                            Text(
                                text = equip.assetCode,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary,
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = when {
                                isOperational -> GlumeSuccessContainer
                                isCalibrationDue -> GlumeWarningContainer
                                else -> GlumeAlertContainer
                            }
                        ) {
                            Text(
                                text = equip.status.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = when {
                                    isOperational -> GlumeSuccessMint
                                    isCalibrationDue -> GlumeWarningAmber
                                    else -> GlumeAlertCoral
                                },
                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = equip.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )

                    Text(
                        text = "Department: ${equip.department} · Location: ${equip.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )

                    HorizontalDivider(color = GlumeBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Last Serviced", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(equip.lastServiceDate, style = MaterialTheme.typography.bodySmall, color = GlumeTextSecondary)
                        }
                        Column {
                            Text("Next Due Date", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(equip.nextServiceDue, style = MaterialTheme.typography.bodySmall, color = if (isCalibrationDue) GlumeWarningAmber else GlumeTextPrimary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "In-Charge: ${equip.inChargeContact}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumePrimaryPurpleLight
                        )

                        OutlinedButton(
                            onClick = { selectedEquipmentForMaint = equip },
                            shape = PillShape,
                            border = BorderStroke(1.dp, GlumeBorder),
                            contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = 2.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 30.dp)
                        ) {
                            Text("Update Status", style = MaterialTheme.typography.labelSmall, color = GlumeTextPrimary)
                        }
                    }
                }
            }
        }
    }

    // Status update dialog
    selectedEquipmentForMaint?.let { equip ->
        var status by remember { mutableStateOf(equip.status) }

        AlertDialog(
            onDismissRequest = { selectedEquipmentForMaint = null },
            title = {
                Text(
                    text = "Update ${equip.assetCode}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(equip.name, style = MaterialTheme.typography.bodyMedium, color = GlumeTextPrimary)

                    Text("Select Operational Status:", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)

                    listOf("OPERATIONAL", "CALIBRATION_DUE", "UNDER_MAINTENANCE").forEach { opt ->
                        Surface(
                            onClick = { status = opt },
                            shape = PillShape,
                            color = if (status == opt) GlumePrimaryPurple else GlumeSurfaceCard,
                            border = BorderStroke(1.dp, if (status == opt) GlumePrimaryPurpleVariant else GlumeBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = opt.replace("_", " "),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (status == opt) GlumeTextPrimary else GlumeTextSecondary,
                                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateEquipment(equip.copy(status = status))
                        selectedEquipmentForMaint = null
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple)
                ) {
                    Text("Save Status", style = MaterialTheme.typography.labelSmall)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEquipmentForMaint = null }) {
                    Text("Cancel", color = GlumeTextSecondary)
                }
            },
            containerColor = GlumeSurfaceCard,
            tonalElevation = 6.dp
        )
    }
}
