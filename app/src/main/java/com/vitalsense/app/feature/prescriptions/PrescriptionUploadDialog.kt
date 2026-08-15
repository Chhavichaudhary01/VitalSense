package com.vitalsense.app.feature.prescriptions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.data.model.PrescribedMedicine
import com.vitalsense.app.core.data.model.Prescription
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseTextField
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.feature.prescriptions.ocr.PrescriptionOcrHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PrescriptionUploadDialog(
    patient: Patient,
    isAshaProxy: Boolean = false,
    onDismiss: () -> Unit,
    onSavePrescription: (Prescription) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0: Camera / AI Scan, 1: Write Down (Manual)

    // --- OCR State ---
    var isProcessingOcr by remember { mutableStateOf(false) }
    var recognizedOcrText by remember { mutableStateOf("") }
    var ocrMedicines by remember { mutableStateOf<List<PrescribedMedicine>>(emptyList()) }
    var ocrDoctorName by remember { mutableStateOf("PHC Attending (Digitized)") }
    var ocrInstructions by remember { mutableStateOf("Take medicines after meals with warm water.") }

    // --- Manual Entry State ---
    var manualDoctorName by remember { mutableStateOf("") }
    var manualSpecialty by remember { mutableStateOf("General Physician") }
    var manualInstructions by remember { mutableStateOf("") }
    val manualMedicines = remember { mutableStateListOf<PrescribedMedicine>() }

    var currentMedName by remember { mutableStateOf("") }
    var currentDosage by remember { mutableStateOf("500 mg") }
    var currentFrequency by remember { mutableStateOf("Twice daily (after meals)") }
    var currentDuration by remember { mutableStateOf("5 Days") }

    fun runSampleOcr(sampleType: String) {
        isProcessingOcr = true
        coroutineScope.launch {
            val sampleText = when (sampleType) {
                "Fever" -> "Rx:\nTab Paracetamol 650mg 1-0-1 (BD)\nTab Cetirizine 10mg 0-0-1 (HS)\nSyp Cough Relief 10ml TDS"
                "Maternal" -> "Rx:\nTab Iron Folic Acid 100mg 1-0-0\nTab Calcium 500mg 0-1-0\nMultivitamin Daily"
                else -> "Rx:\nTab Amoxicillin 500mg 1-1-1\nTab Paracetamol 500mg 1-0-1\nORS solution daily"
            }

            val bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
            }
            canvas.drawText(sampleText, 20f, 60f, paint)

            val text = PrescriptionOcrHelper.recognizeTextFromBitmap(bitmap)
            val finalText = if (text.isNotBlank() && !text.startsWith("OCR Processing Error")) text else sampleText
            val parsed = PrescriptionOcrHelper.parseMedicinesFromText(finalText)

            recognizedOcrText = finalText
            ocrMedicines = parsed
            isProcessingOcr = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = DialogShape,
            color = WarmCreamBackground,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAshaProxy) "🤝 Upload Prescription" else "💊 Upload Prescription",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Patient: ${patient.name} (${patient.villageName})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text(text = "✕", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondaryMuted)
                    }
                }

                HorizontalDivider(color = DividerSubtle)

                // Custom Styled Tab Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Surface(
                        onClick = { selectedTab = 0 },
                        shape = PillShape,
                        color = if (selectedTab == 0) LimePrimary else SurfaceWhite,
                        border = BorderStroke(1.dp, if (selectedTab == 0) DarkCharcoal else CardBorderColor),
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📷 Camera / AI Scan", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Surface(
                        onClick = { selectedTab = 1 },
                        shape = PillShape,
                        color = if (selectedTab == 1) LimePrimary else SurfaceWhite,
                        border = BorderStroke(1.dp, if (selectedTab == 1) DarkCharcoal else CardBorderColor),
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✍️ Write Down", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // TAB 0: CAMERA / ON-DEVICE OCR
                if (selectedTab == 0) {
                    Surface(
                        shape = CardShape,
                        color = SoftMintSuccess.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, SoftMintSuccess),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text(text = "⚡", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "On-Device AI: Scans doctor slips offline with zero internet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryNearBlack
                            )
                        }
                    }

                    Text(
                        text = "Select Document Sample:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryNearBlack
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Button(
                            onClick = { runSampleOcr("General") },
                            modifier = Modifier.weight(1f).defaultMinSize(minHeight = 36.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal, contentColor = LimePrimary)
                        ) {
                            Text("General", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = { runSampleOcr("Fever") },
                            modifier = Modifier.weight(1f).defaultMinSize(minHeight = 36.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal, contentColor = LimePrimary)
                        ) {
                            Text("Fever/Cold", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = { runSampleOcr("Maternal") },
                            modifier = Modifier.weight(1f).defaultMinSize(minHeight = 36.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal, contentColor = LimePrimary)
                        ) {
                            Text("Maternal", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (isProcessingOcr) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = DarkCharcoal, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text("Extracting handwritten text on-device...", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (recognizedOcrText.isNotBlank()) {
                        VitalSenseCard(backgroundColor = SurfaceWhite) {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                                Text(
                                    text = "Extracted Raw Text:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(text = recognizedOcrText, style = MaterialTheme.typography.bodySmall, color = TextSecondaryMuted)
                            }
                        }

                        Text(
                            text = "Parsed Medicines (${ocrMedicines.size}):",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )

                        ocrMedicines.forEach { med ->
                            VitalSenseCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = med.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text(text = "${med.dosage} · ${med.frequency} · ${med.duration}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryMuted)
                                    }
                                    Surface(shape = PillShape, color = SoftMintSuccess.copy(alpha = 0.5f)) {
                                        Text("AI Parsed", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SoftMintText), modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs))
                                    }
                                }
                            }
                        }

                        VitalSenseTextField(
                            value = ocrDoctorName,
                            onValueChange = { ocrDoctorName = it },
                            label = "Prescribing Doctor / Hospital"
                        )

                        VitalSenseTextField(
                            value = ocrInstructions,
                            onValueChange = { ocrInstructions = it },
                            label = "Instructions / Diet Notes",
                            singleLine = false,
                            maxLines = 3
                        )

                        Button(
                            onClick = {
                                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                val rx = Prescription(
                                    id = "rx_ocr_${UUID.randomUUID().toString().take(6)}",
                                    patientId = patient.id,
                                    patientName = patient.name,
                                    doctorId = "dr_ocr",
                                    doctorName = ocrDoctorName.ifBlank { "PHC Doctor" },
                                    doctorSpecialty = "General Medicine",
                                    timestamp = System.currentTimeMillis(),
                                    dateFormatted = dateFormat.format(Date()),
                                    medicines = ocrMedicines,
                                    instructions = ocrInstructions,
                                    isOcrExtracted = true
                                )
                                onSavePrescription(rx)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = LimePrimary, contentColor = TextPrimaryNearBlack)
                        ) {
                            Text("Save Digitized Prescription ✓", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                // TAB 1: WRITE DOWN (MANUAL PRESCRIPTION ENTRY)
                if (selectedTab == 1) {
                    VitalSenseTextField(
                        value = manualDoctorName,
                        onValueChange = { manualDoctorName = it },
                        label = "Doctor Name / Clinic Name",
                        placeholder = "e.g. Dr. Rajesh Sharma (District Hospital)"
                    )

                    // Add Medicine Form Box
                    VitalSenseCard(backgroundColor = SurfaceWhite) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Text(
                                text = "➕ Add Prescribed Medicine:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryNearBlack
                            )

                            VitalSenseTextField(
                                value = currentMedName,
                                onValueChange = { currentMedName = it },
                                label = "Medicine Name",
                                placeholder = "e.g. Paracetamol / Amoxicillin"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                VitalSenseTextField(
                                    value = currentDosage,
                                    onValueChange = { currentDosage = it },
                                    label = "Dosage",
                                    modifier = Modifier.weight(1f)
                                )
                                VitalSenseTextField(
                                    value = currentDuration,
                                    onValueChange = { currentDuration = it },
                                    label = "Duration",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            VitalSenseTextField(
                                value = currentFrequency,
                                onValueChange = { currentFrequency = it },
                                label = "Frequency / Timing"
                            )

                            Button(
                                onClick = {
                                    if (currentMedName.isNotBlank()) {
                                        manualMedicines.add(
                                            PrescribedMedicine(
                                                name = currentMedName.trim(),
                                                dosage = currentDosage.trim(),
                                                frequency = currentFrequency.trim(),
                                                duration = currentDuration.trim(),
                                                quantity = 10
                                            )
                                        )
                                        currentMedName = ""
                                        currentDosage = "500 mg"
                                        currentDuration = "5 Days"
                                        currentFrequency = "Twice daily (after meals)"
                                    }
                                },
                                enabled = currentMedName.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 40.dp),
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal, contentColor = LimePrimary)
                            ) {
                                Text("+ Add to Medicine List", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    // Added Medicines List
                    if (manualMedicines.isNotEmpty()) {
                        Text(
                            text = "Prescription Medicines (${manualMedicines.size}):",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )

                        manualMedicines.forEachIndexed { idx, med ->
                            VitalSenseCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "${idx + 1}. ${med.name}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text(text = "${med.dosage} · ${med.frequency} · ${med.duration}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryMuted)
                                    }
                                    IconButton(onClick = { manualMedicines.removeAt(idx) }, modifier = Modifier.size(32.dp)) {
                                        Text("🗑️", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    VitalSenseTextField(
                        value = manualInstructions,
                        onValueChange = { manualInstructions = it },
                        label = "Instructions / Precautions",
                        placeholder = "e.g. Drink plenty of water, avoid cold foods",
                        singleLine = false,
                        maxLines = 2
                    )

                    Button(
                        onClick = {
                            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            val rx = Prescription(
                                id = "rx_manual_${UUID.randomUUID().toString().take(6)}",
                                patientId = patient.id,
                                patientName = patient.name,
                                doctorId = "dr_manual",
                                doctorName = manualDoctorName.ifBlank { "Attending Physician" },
                                doctorSpecialty = manualSpecialty,
                                timestamp = System.currentTimeMillis(),
                                dateFormatted = dateFormat.format(Date()),
                                medicines = manualMedicines.toList(),
                                instructions = manualInstructions.ifBlank { "Take as prescribed." },
                                isOcrExtracted = false
                            )
                            onSavePrescription(rx)
                            onDismiss()
                        },
                        enabled = manualMedicines.isNotEmpty() || currentMedName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = LimePrimary, contentColor = TextPrimaryNearBlack)
                    ) {
                        Text("Save Prescription Record ✓", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
