package com.vitalsense.app.feature.doctor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionComposerDialog(
    patient: Patient?,
    patientNameFallback: String,
    caseId: String,
    dispensaryStock: List<DispensaryItem>,
    onDismiss: () -> Unit,
    onIssuePrescription: (medicines: List<PrescribedMedicine>, instructions: String) -> Unit
) {
    var instructions by remember { mutableStateOf("Take medications after meals as directed. Drink plenty of boiled lukewarm water.") }
    val medicinesList = remember {
        mutableStateListOf(
            PrescribedMedicine("Amoxicillin 500mg", "1 capsule", "3 times daily after food", "5 days", 15),
            PrescribedMedicine("Paracetamol 650mg", "1 tablet", "SOS (if fever > 100°F)", "3 days", 6)
        )
    }

    var newMedName by remember { mutableStateOf("") }
    var newMedDosage by remember { mutableStateOf("1 tablet") }
    var newMedFrequency by remember { mutableStateOf("Twice daily after meals") }
    var newMedDuration by remember { mutableStateOf("5 days") }
    var newMedQuantity by remember { mutableStateOf("10") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = CardShape,
            color = WarmCreamBackground,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💊 Issue Structured Prescription",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Patient: ${patient?.name ?: patientNameFallback} (${patient?.villageName ?: "Rural PHC"})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text(text = "✕", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Current Medicines in this Prescription
                    item {
                        Text(
                            text = "Prescribed Medicines (${medicinesList.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                    }

                    itemsIndexed(medicinesList) { index, med ->
                        // Dispensary availability check
                        val stockItem = dispensaryStock.find {
                            it.medicineName.contains(med.name.split(" ").firstOrNull() ?: "", ignoreCase = true)
                        }

                        VitalSenseCard(backgroundColor = SurfaceWhite, elevation = 1.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = med.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimaryNearBlack
                                    )
                                    IconButton(
                                        onClick = { medicinesList.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text(text = "🗑️", fontSize = 12.sp)
                                    }
                                }

                                Text(
                                    text = "Dosage: ${med.dosage} · ${med.frequency}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimaryNearBlack
                                )
                                Text(
                                    text = "Duration: ${med.duration} · Qty: ${med.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )

                                // Inventory Badge
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (stockItem != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (stockItem.isLowStock) CoralAlert else SoftMintSuccess)
                                        )
                                        Text(
                                            text = if (stockItem.isLowStock)
                                                "Dispensary: Low Stock (${stockItem.availableQuantity} left)"
                                            else "Dispensary: In Stock (${stockItem.availableQuantity} available)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (stockItem.isLowStock) CoralAlert else Color(0xFF2E7D32)
                                        )
                                    } else {
                                        Text(
                                            text = "Dispensary: External / Verified",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Add Another Medicine Form
                    item {
                        VitalSenseCard(backgroundColor = WarmCreamBackground) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "➕ Add Medicine",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryNearBlack
                                )

                                OutlinedTextField(
                                    value = newMedName,
                                    onValueChange = { newMedName = it },
                                    label = { Text("Medicine Name (e.g. Cetirizine 10mg)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceWhite,
                                        unfocusedContainerColor = SurfaceWhite
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newMedDosage,
                                        onValueChange = { newMedDosage = it },
                                        label = { Text("Dosage") },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceWhite,
                                            unfocusedContainerColor = SurfaceWhite
                                        )
                                    )
                                    OutlinedTextField(
                                        value = newMedQuantity,
                                        onValueChange = { newMedQuantity = it },
                                        label = { Text("Qty") },
                                        modifier = Modifier.weight(0.7f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceWhite,
                                            unfocusedContainerColor = SurfaceWhite
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newMedFrequency,
                                        onValueChange = { newMedFrequency = it },
                                        label = { Text("Frequency (e.g. Twice daily)") },
                                        modifier = Modifier.weight(1.3f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceWhite,
                                            unfocusedContainerColor = SurfaceWhite
                                        )
                                    )
                                    OutlinedTextField(
                                        value = newMedDuration,
                                        onValueChange = { newMedDuration = it },
                                        label = { Text("Duration") },
                                        modifier = Modifier.weight(0.9f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceWhite,
                                            unfocusedContainerColor = SurfaceWhite
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (newMedName.isNotBlank()) {
                                            val qty = newMedQuantity.toIntOrNull() ?: 10
                                            medicinesList.add(
                                                PrescribedMedicine(
                                                    name = newMedName.trim(),
                                                    dosage = newMedDosage.trim(),
                                                    frequency = newMedFrequency.trim(),
                                                    duration = newMedDuration.trim(),
                                                    quantity = qty
                                                )
                                            )
                                            newMedName = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal)
                                ) {
                                    Text(text = "Add to Prescription", color = LimePrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Clinical Instructions
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Clinical Instructions & Diet Advice",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryNearBlack
                            )
                            OutlinedTextField(
                                value = instructions,
                                onValueChange = { instructions = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceWhite,
                                    unfocusedContainerColor = SurfaceWhite
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = PillShape
                    ) {
                        Text(text = "Cancel")
                    }

                    Button(
                        onClick = {
                            if (medicinesList.isNotEmpty()) {
                                onIssuePrescription(medicinesList.toList(), instructions)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = LimePrimary, contentColor = TextPrimaryNearBlack)
                    ) {
                        Text(text = "Save & Issue (Rx) ✓", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
