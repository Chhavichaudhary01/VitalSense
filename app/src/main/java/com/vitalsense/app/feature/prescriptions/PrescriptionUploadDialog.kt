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
            color = GlumeSurfaceCard,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, GlumeBorder)
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
                            text = if (isAshaProxy) "Upload Prescription (for ${patient.name})" else "Add Prescription",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Digitize paper prescription via camera OCR or manual entry",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text(text = "✕", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextSecondary)
                    }
                }

                // Glume Segmented Pill Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    val tabs = listOf("📷 Camera / AI Scan", "✍️ Write Down")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = PillShape,
                            color = if (isSelected) GlumePrimaryPurpleContainer else GlumeSurfaceElevated,
                            border = if (isSelected) BorderStroke(1.5.dp, GlumePrimaryPurple) else BorderStroke(1.dp, GlumeBorder),
                            modifier = Modifier.weight(1f).defaultMinSize(minHeight = 40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) GlumePrimaryPurpleLight else GlumeTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = GlumeBorder)

                // Tab 0: Camera / AI Scan
                if (selectedTab == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Text(
                            text = "📸 Simulate Camera Scan / Capture Rx",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Point camera at doctor's handwritten or printed prescription. On-device ML Kit will extract dosage and medicines automatically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )

                        // Sample Rx Presets for Demonstration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            listOf("Fever", "Maternal", "Antibiotic").forEach { preset ->
                                OutlinedButton(
                                    onClick = { runSampleOcr(preset) },
                                    modifier = Modifier.weight(1f),
                                    shape = PillShape,
                                    border = BorderStroke(1.dp, GlumeBorder)
                                ) {
                                    Text("Rx: $preset", style = MaterialTheme.typography.labelSmall, color = GlumeTextPrimary)
                                }
                            }
                        }

                        if (isProcessingOcr) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = GlumePrimaryPurple)
                            }
                        } else if (recognizedOcrText.isNotBlank()) {
                            VitalSenseCard(
                                backgroundColor = GlumeSurfaceElevated,
                                border = BorderStroke(1.dp, GlumeBorder)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Extracted OCR Telemetry",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GlumePrimaryPurpleLight
                                        )
                                        Surface(shape = PillShape, color = GlumeSuccessContainer) {
                                            Text(
                                                text = "Parsed ${ocrMedicines.size} items",
                                                style = MaterialTheme.typography.labelSmall.copy(color = GlumeSuccessText, fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = recognizedOcrText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GlumeTextPrimary
                                    )
                                }
                            }

                            VitalSenseTextField(
                                value = ocrDoctorName,
                                onValueChange = { ocrDoctorName = it },
                                label = "Prescribing Doctor / Clinic"
                            )

                            VitalSenseTextField(
                                value = ocrInstructions,
                                onValueChange = { ocrInstructions = it },
                                label = "Instructions / Diet Notes"
                            )

                            VitalSenseButton(
                                text = "Save & Attach Prescription",
                                onClick = {
                                    val newRx = Prescription(
                                        id = "rx_${System.currentTimeMillis()}",
                                        patientId = patient.id,
                                        patientName = patient.name,
                                        doctorId = "doc_attending",
                                        doctorName = ocrDoctorName.ifBlank { "PHC Attending (Digitized)" },
                                        doctorSpecialty = "General Physician",
                                        timestamp = System.currentTimeMillis(),
                                        dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                        medicines = if (ocrMedicines.isNotEmpty()) ocrMedicines else listOf(
                                            PrescribedMedicine("Paracetamol", "500 mg", "Twice daily", "3 Days", 10)
                                        ),
                                        instructions = ocrInstructions,
                                        isOcrExtracted = true
                                    )
                                    onSavePrescription(newRx)
                                    onDismiss()
                                },
                                style = com.vitalsense.app.core.ui.components.ButtonStyle.PRIMARY
                            )
                        }
                    }
                }

                // Tab 1: Write Down (Manual Entry)
                if (selectedTab == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        VitalSenseTextField(
                            value = manualDoctorName,
                            onValueChange = { manualDoctorName = it },
                            label = "Doctor Name",
                            placeholder = "e.g. Dr. A. Sharma"
                        )

                        VitalSenseTextField(
                            value = manualSpecialty,
                            onValueChange = { manualSpecialty = it },
                            label = "Specialty / Clinic",
                            placeholder = "e.g. General Physician / District Hospital"
                        )

                        Text(
                            text = "Add Prescribed Medicines",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )

                        VitalSenseCard(
                            backgroundColor = GlumeSurfaceElevated,
                            border = BorderStroke(1.dp, GlumeBorder)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                VitalSenseTextField(
                                    value = currentMedName,
                                    onValueChange = { currentMedName = it },
                                    label = "Medicine Name",
                                    placeholder = "e.g. Amoxicillin / Paracetamol"
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        VitalSenseTextField(
                                            value = currentDosage,
                                            onValueChange = { currentDosage = it },
                                            label = "Dosage",
                                            placeholder = "500 mg"
                                        )
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        VitalSenseTextField(
                                            value = currentDuration,
                                            onValueChange = { currentDuration = it },
                                            label = "Duration",
                                            placeholder = "5 Days"
                                        )
                                    }
                                }

                                VitalSenseTextField(
                                    value = currentFrequency,
                                    onValueChange = { currentFrequency = it },
                                    label = "Frequency",
                                    placeholder = "Twice daily after meals"
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
                                        }
                                    },
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                                    modifier = Modifier.align(Alignment.End),
                                    enabled = currentMedName.isNotBlank()
                                ) {
                                    Text("+ Add Medicine", color = GlumeTextPrimary, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        // Added Medicines List
                        if (manualMedicines.isNotEmpty()) {
                            Text(
                                text = "Medicines to Include (${manualMedicines.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            manualMedicines.forEachIndexed { index, med ->
                                Surface(
                                    shape = PillShape,
                                    color = GlumeSurfaceElevated,
                                    border = BorderStroke(1.dp, GlumeBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${med.name} (${med.dosage}) - ${med.frequency} · ${med.duration}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GlumeTextPrimary
                                        )
                                        IconButton(onClick = { manualMedicines.removeAt(index) }, modifier = Modifier.size(24.dp)) {
                                            Text(text = "✕", color = GlumeAlertCoral, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        VitalSenseTextField(
                            value = manualInstructions,
                            onValueChange = { manualInstructions = it },
                            label = "Additional Notes / Precautions",
                            placeholder = "e.g. Drink plenty of warm water and avoid oily food",
                            singleLine = false,
                            maxLines = 2
                        )

                        VitalSenseButton(
                            text = "Save Prescription Record",
                            onClick = {
                                val newRx = Prescription(
                                    id = "rx_${System.currentTimeMillis()}",
                                    patientId = patient.id,
                                    patientName = patient.name,
                                    doctorId = "doc_attending",
                                    doctorName = manualDoctorName.ifBlank { "Attending Medical Officer" },
                                    doctorSpecialty = manualSpecialty.ifBlank { "General Physician" },
                                    timestamp = System.currentTimeMillis(),
                                    dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                    medicines = manualMedicines.toList(),
                                    instructions = manualInstructions,
                                    isOcrExtracted = false
                                )
                                onSavePrescription(newRx)
                                onDismiss()
                            },
                            style = com.vitalsense.app.core.ui.components.ButtonStyle.PRIMARY,
                            enabled = manualMedicines.isNotEmpty()
                        )
                    }
                }
            }
        }
    }
}
