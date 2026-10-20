package com.vitalsense.app.feature.patient
import androidx.compose.ui.res.stringResource
import com.vitalsense.app.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@Composable
fun PrescriptionsListScreen(
    prescriptions: List<Prescription>,
    patient: Patient? = null
) {
    var selectedMedicineForNearby by remember { mutableStateOf<PrescribedMedicine?>(null) }
    var sharingPrescription by remember { mutableStateOf<Prescription?>(null) }

    val fallbackPatient = patient ?: Patient(
        id = "default_pat",
        name = "Patient",
        age = 35,
        gender = "M",
        phone = "+91 9876543210",
        villageId = "v_sundarpura",
        villageName = "Sundarpura",
        ashaWorkerId = "asha_1",
        ashaWorkerName = "Priya Devi",
        currentRiskLevel = SeverityLevel.LOW,
        lastCondition = "General Checkup",
        lastVisitDate = "Today",
        nextAppointmentDate = null,
        emergencyContact = "+91 9876543211"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.myPrescriptions),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = VS_OnBackground
            )
        }

        if (prescriptions.isEmpty()) {
            item {
                VitalSenseCard {
                    Text(
                        text = stringResource(R.string.noPrescriptionsFound),
                        style = MaterialTheme.typography.bodyMedium,
                        color = VS_OnSurfaceVariant
                    )
                }
            }
        } else {
            items(prescriptions) { rx ->
                VitalSenseCard {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = rx.doctorName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VS_OnBackground
                                )
                                Text(
                                    text = "${rx.doctorSpecialty} · ${rx.dateFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VS_OnSurfaceVariant
                                )
                            }
                            if (rx.isOcrExtracted) {
                                Surface(shape = PillShape, color = VS_SuccessContainer) {
                                    Text(
                                        text = stringResource(R.string.aiScannedBadge),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = VS_OnSuccessContainer),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = VS_Outline, modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = stringResource(R.string.prescribedMedicinesLabel),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = VS_OnBackground
                        )

                        rx.medicines.forEach { med ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "• ${med.name} (${med.dosage})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = VS_OnBackground
                                    )
                                    Text(
                                        text = "${med.frequency} · ${med.duration}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = VS_OnSurfaceVariant
                                    )
                                    if (med.hasAlternativeAvailable) {
                                        Text(
                                            text = stringResource(R.string.docSuggestedAlternativePlain),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = VS_Error
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { selectedMedicineForNearby = med },
                                    modifier = Modifier.height(34.dp),
                                    shape = PillShape,
                                    border = BorderStroke(1.dp, VS_Primary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.findNearbyLink),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VS_Primary
                                    )
                                }
                            }
                        }

                        if (rx.instructions.isNotBlank()) {
                            Text(
                                text = "Instructions: ${rx.instructions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = VS_OnSurfaceVariant
                            )
                        }

                        HorizontalDivider(color = VS_Outline, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬 Rural SMS / WhatsApp",
                                style = MaterialTheme.typography.labelSmall,
                                color = VS_OnSurfaceVariant
                            )

                            Button(
                                onClick = { sharingPrescription = rx },
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VS_PrimaryContainer,
                                    contentColor = VS_Primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("📲", fontSize = 12.sp)
                                    Text(
                                        text = "Share via SMS / WhatsApp",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedMedicineForNearby?.let { med ->
        FindMedicineNearbySheet(
            patient = fallbackPatient,
            medicine = med,
            onDismiss = { selectedMedicineForNearby = null }
        )
    }

    sharingPrescription?.let { rx ->
        com.vitalsense.app.feature.prescriptions.SharePrescriptionDialog(
            prescription = rx,
            patientPhoneDefault = fallbackPatient.phone,
            onDismiss = { sharingPrescription = null }
        )
    }
}