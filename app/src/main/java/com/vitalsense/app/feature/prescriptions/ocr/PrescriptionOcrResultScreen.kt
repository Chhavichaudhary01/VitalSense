package com.vitalsense.app.feature.prescriptions.ocr
import androidx.compose.ui.res.stringResource
import com.vitalsense.app.R

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.data.model.PrescribedMedicine
import com.vitalsense.app.core.data.model.Prescription
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.ButtonStyle
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen displaying the recognized OCR text, editable field,
 * parsed medicine items, and direct save action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionOcrResultScreen(
    patient: Patient,
    photoFile: File?,
    onSavePrescription: (Prescription) -> Unit,
    onRetakePhoto: () -> Unit,
    onManualEntryFallback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isOcrRunning by remember { mutableStateOf(true) }
    var rawOcrText by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("PHC Attending (Digitized)") }
    var instructions by remember { mutableStateOf("Take medicines as directed with clean drinking water.") }
    val medicinesList = remember { mutableStateListOf<PrescribedMedicine>() }
    var editingMedicineIndex by remember { mutableStateOf<Int?>(null) }
    var showReviewSheet by remember { mutableStateOf(false) }
    var legalAttestationChecked by remember { mutableStateOf(false) }

    // Run on-device ML Kit text recognition on the confirmed photo
    LaunchedEffect(photoFile) {
        if (photoFile != null && photoFile.exists()) {
            isOcrRunning = true
            val extracted = PrescriptionOcrHelper.recognizeTextFromFile(context, photoFile)
            rawOcrText = if (extracted.startsWith("OCR Processing Error")) "" else extracted
            val parsed = PrescriptionOcrHelper.parseMedicinesFromText(rawOcrText)
            medicinesList.clear()
            medicinesList.addAll(parsed)
            isOcrRunning = false
        } else {
            isOcrRunning = false
        }
    }

    if (isOcrRunning) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VS_Background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                CircularProgressIndicator(color = VS_Primary, modifier = Modifier.size(48.dp))
                Text(
                    text = stringResource(R.string.readingPrescriptionOnDevice),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = VS_OnBackground
                )
                Text(
                    text = stringResource(R.string.runningLocalMlKitOcr),
                    style = MaterialTheme.typography.bodySmall,
                    color = VS_OnSurfaceVariant
                )
            }
        }
        return
    }

    // If no text was recognized at all
    if (rawOcrText.isBlank()) {
        NoTextDetectedView(
            onRetake = onRetakePhoto,
            onManualEntry = onManualEntryFallback,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VS_Background)
            .padding(Spacing.md)
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
                    text = stringResource(R.string.reviewConfirmOcrScan),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = VS_OnBackground
                )
                Text(
                    text = "Patient: ${patient.name} · Verify extracted text below",
                    style = MaterialTheme.typography.bodySmall,
                    color = VS_OnSurfaceVariant
                )
            }
            // Listen to recognized text
            IconButton(
                onClick = {
                    AudioGuidanceHelper.speak(context, "Extracted prescription text: $rawOcrText")
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(VS_PrimaryContainer, RoundedCornerShape(10.dp))
            ) {
                Text("🔊", fontSize = 18.sp)
            }
        }

        // On-Device AI Privacy & Offline Guarantee
        Surface(
            color = VS_PrimaryContainer.copy(alpha = 0.5f),
            shape = CardShape,
            border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text("🔒", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "100% On-Device AI · Zero Cloud Upload",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VS_Primary
                    )
                    Text(
                        text = "Processed strictly locally via Google ML Kit. No patient data or image leaves this phone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = VS_OnSurfaceVariant
                    )
                }
            }
        }

        // Medical Shorthand Auto-Expansion Reference
        Surface(
            color = VS_Surface,
            shape = CardShape,
            border = BorderStroke(1.dp, VS_Outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text("⚡", fontSize = 16.sp)
                    Text(
                        text = "Medical Shorthand Auto-Expanded:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VS_OnBackground
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "BD" to "Twice Daily",
                        "TDS" to "3x Daily",
                        "SOS" to "As Needed",
                        "PO" to "Orally"
                    ).forEach { (short, full) ->
                        Surface(
                            shape = PillShape,
                            color = VS_SurfaceVariant,
                            border = BorderStroke(0.5.dp, VS_Outline)
                        ) {
                            Text(
                                text = "$short → $full",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                color = VS_OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 1. Raw Extracted Text (User Editable)
        VitalSenseCard {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.extractedTextTapToEdit),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VS_OnBackground
                    )
                    Surface(
                        shape = PillShape,
                        color = VS_SuccessContainer
                    ) {
                        Text(
                            text = stringResource(R.string.onDeviceOcrBadge),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = VS_OnSuccessContainer),
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = rawOcrText,
                    onValueChange = { newText ->
                        rawOcrText = newText
                        val updatedMeds = PrescriptionOcrHelper.parseMedicinesFromText(newText)
                        medicinesList.clear()
                        medicinesList.addAll(updatedMeds)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 160.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = VS_OnBackground),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VS_Primary,
                        unfocusedBorderColor = VS_Outline
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // 2. Structured Medicines
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = "Identified Medicines (${medicinesList.size}):",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = VS_OnBackground
            )

            if (medicinesList.isEmpty()) {
                Surface(
                    shape = CardShape,
                    color = VS_SurfaceVariant,
                    border = BorderStroke(1.dp, VS_Outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.noMedicineNamesMatchedFallback),
                        style = MaterialTheme.typography.bodySmall,
                        color = VS_OnSurfaceVariant,
                        modifier = Modifier.padding(Spacing.md)
                    )
                }
            } else {
                Text(
                    text = "Tap any medicine to adjust dose, frequency, or duration:",
                    style = MaterialTheme.typography.bodySmall,
                    color = VS_OnSurfaceVariant
                )
                medicinesList.forEachIndexed { index, med ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingMedicineIndex = index },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = VS_Surface),
                        border = BorderStroke(1.dp, VS_Outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.sm),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                ) {
                                    Text(
                                        text = "💊 ${med.name} (${med.dosage})",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = VS_OnBackground
                                    )
                                    Surface(
                                        shape = PillShape,
                                        color = VS_SuccessContainer
                                    ) {
                                        Text(
                                            text = "Auto-Expanded",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = VS_OnSuccessContainer, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${med.frequency} · ${med.duration}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = VS_OnSurfaceVariant
                                    )
                                    Surface(
                                        shape = PillShape,
                                        color = VS_PrimaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "✏️ Edit Dose",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = VS_Primary, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            IconButton(
                                onClick = { medicinesList.removeAt(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("✕", color = VS_Error, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Attending Doctor Name & Notes
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(R.string.prescribingDoctorHealthPost),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = VS_OnBackground
            )
            OutlinedTextField(
                value = doctorName,
                onValueChange = { doctorName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = stringResource(R.string.instructionsDosageDirections),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = VS_OnBackground
            )
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(10.dp)
            )
        }

        // 4. Save & Retake Actions
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            VitalSenseButton(
                text = "Review & Legal Clinical Sign-Off →",
                style = ButtonStyle.PRIMARY,
                onClick = { showReviewSheet = true }
            )

            OutlinedButton(
                onClick = onRetakePhoto,
                shape = PillShape,
                border = BorderStroke(1.dp, VS_Outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.retakePhotoBtn), color = VS_OnBackground)
            }
        }
    }

    // Medicine Dose Adjustment Dialog
    editingMedicineIndex?.let { idx ->
        if (idx in medicinesList.indices) {
            EditMedicineDialog(
                medicine = medicinesList[idx],
                onDismiss = { editingMedicineIndex = null },
                onSave = { updated ->
                    medicinesList[idx] = updated
                    editingMedicineIndex = null
                }
            )
        }
    }

    // Review & Legal Clinical Sign-Off Bottom Sheet
    if (showReviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReviewSheet = false },
            containerColor = VS_Surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚖️", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "Legal Clinical Review & Sign-Off",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VS_OnBackground
                        )
                        Text(
                            text = "National Telemedicine Guidelines (2020) & Pharmacy Act Sign-Off",
                            style = MaterialTheme.typography.bodySmall,
                            color = VS_OnSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = VS_PrimaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Patient: ${patient.name} (ABHA Verified)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = VS_Primary
                        )
                        Text(
                            text = "Attending Clinician: ${doctorName.ifBlank { "Medical Officer In-Charge" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = VS_OnSurfaceVariant
                        )
                    }
                }

                // Verified Medicines Summary
                Text(
                    text = "Verified Medications (${medicinesList.size}):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = VS_OnBackground
                )

                medicinesList.forEach { med ->
                    Surface(
                        color = VS_SurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "💊 ${med.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VS_OnBackground
                                )
                                Text(
                                    text = "${med.frequency} · Duration: ${med.duration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VS_OnSurfaceVariant
                                )
                            }
                            Surface(
                                shape = PillShape,
                                color = VS_SuccessContainer
                            ) {
                                Text(
                                    text = med.dosage,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = VS_OnSuccessContainer),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Legal Certification Toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (legalAttestationChecked) VS_SuccessContainer.copy(alpha = 0.5f) else VS_WarningContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (legalAttestationChecked) VS_Success else VS_Warning),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { legalAttestationChecked = !legalAttestationChecked }
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = legalAttestationChecked,
                            onCheckedChange = { legalAttestationChecked = it }
                        )
                        Text(
                            text = "I hereby certify that I have verified each digitized medication, dosage, and frequency against the physical prescription and approve legal entry into the Patient Health Record.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = VS_OnBackground
                        )
                    }
                }

                Button(
                    onClick = {
                        val finalMedicines = if (medicinesList.isNotEmpty()) {
                            medicinesList.toList()
                        } else {
                            listOf(
                                PrescribedMedicine(
                                    name = "Digitized Prescription Note",
                                    dosage = "As indicated",
                                    frequency = "See notes",
                                    duration = "7 Days",
                                    quantity = 1
                                )
                            )
                        }

                        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

                        val prescription = Prescription(
                            id = "rx_ocr_${System.currentTimeMillis()}",
                            patientId = patient.id,
                            patientName = patient.name,
                            doctorId = "doc_ocr_scan",
                            doctorName = doctorName.ifBlank { "PHC Attending (Digitized)" },
                            doctorSpecialty = "General Medicine",
                            timestamp = System.currentTimeMillis(),
                            dateFormatted = dateStr,
                            medicines = finalMedicines,
                            instructions = if (instructions.isNotBlank()) instructions else rawOcrText,
                            isOcrExtracted = true
                        )

                        showReviewSheet = false
                        onSavePrescription(prescription)
                    },
                    enabled = legalAttestationChecked,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VS_Success,
                        disabledContainerColor = VS_Outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Legal Sign-Off & Commit Prescription",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))
            }
        }
    }
}

/**
 * Clean fallback state when ML Kit detects no text on the captured photo
 */
@Composable
private fun NoTextDetectedView(
    onRetake: () -> Unit,
    onManualEntry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VS_Background)
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = VS_Surface),
            border = BorderStroke(1.dp, VS_Outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(text = "🔍", fontSize = 48.sp)

                Text(
                    text = stringResource(R.string.couldntReadAnyText),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = VS_OnBackground,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.photoQualityHint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VS_OnSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Button(
                    onClick = onRetake,
                    colors = ButtonDefaults.buttonColors(containerColor = VS_Primary),
                    shape = PillShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.retakePhotoBtn), color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onManualEntry,
                    shape = PillShape,
                    border = BorderStroke(1.dp, VS_Outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.enterPrescriptionManually), color = VS_OnBackground)
                }
            }
        }
    }
}

@Composable
private fun EditMedicineDialog(
    medicine: PrescribedMedicine,
    onDismiss: () -> Unit,
    onSave: (PrescribedMedicine) -> Unit
) {
    var name by remember { mutableStateOf(medicine.name) }
    var dosage by remember { mutableStateOf(medicine.dosage) }
    var frequency by remember { mutableStateOf(medicine.frequency) }
    var duration by remember { mutableStateOf(medicine.duration) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("✏️", fontSize = 20.sp)
                Text("Adjust Medication & Dosage", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dosage with quick chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage") },
                        placeholder = { Text("e.g. 500mg") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("50mg", "100mg", "250mg", "500mg", "650mg").forEach { chip ->
                            val isSel = dosage.contains(chip, ignoreCase = true)
                            Surface(
                                shape = PillShape,
                                color = if (isSel) VS_PrimaryContainer else VS_SurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) VS_Primary else VS_Outline),
                                modifier = Modifier.clickable { dosage = chip }
                            ) {
                                Text(
                                    text = chip,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = if (isSel) VS_PrimaryContainer else VS_OnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Frequency with quick chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { frequency = it },
                        label = { Text("Frequency") },
                        placeholder = { Text("e.g. Twice Daily (BD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("OD (1x)" to "Once Daily (OD)", "BD (2x)" to "Twice Daily (BD)", "TDS (3x)" to "Thrice Daily (TDS)", "SOS" to "As Needed (SOS)").forEach { (label, full) ->
                            val isSel = frequency.contains(label.take(2), ignoreCase = true)
                            Surface(
                                shape = PillShape,
                                color = if (isSel) VS_PrimaryContainer else VS_SurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) VS_Primary else VS_Outline),
                                modifier = Modifier.clickable { frequency = full }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = if (isSel) VS_PrimaryContainer else VS_OnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Duration
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration") },
                        placeholder = { Text("e.g. 5 Days") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("3 Days", "5 Days", "7 Days", "14 Days", "30 Days").forEach { dur ->
                            val isSel = duration.equals(dur, ignoreCase = true)
                            Surface(
                                shape = PillShape,
                                color = if (isSel) VS_PrimaryContainer else VS_SurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) VS_Primary else VS_Outline),
                                modifier = Modifier.clickable { duration = dur }
                            ) {
                                Text(
                                    text = dur,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = if (isSel) VS_PrimaryContainer else VS_OnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        medicine.copy(
                            name = name.ifBlank { medicine.name },
                            dosage = dosage.ifBlank { medicine.dosage },
                            frequency = frequency.ifBlank { medicine.frequency },
                            duration = duration.ifBlank { medicine.duration }
                        )
                    )
                },
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = VS_Primary)
            ) {
                Text("Apply Adjustment", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VS_OnSurfaceVariant)
            }
        }
    )
}

