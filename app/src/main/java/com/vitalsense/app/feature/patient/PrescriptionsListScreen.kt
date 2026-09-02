package com.vitalsense.app.feature.patient

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
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@Composable
fun PrescriptionsListScreen(
    prescriptions: List<Prescription>,
    patient: Patient? = null
) {
    var selectedMedicineForNearby by remember { mutableStateOf<PrescribedMedicine?>(null) }

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
                text = "My Prescriptions",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = GlumeTextPrimary
            )
        }

        if (prescriptions.isEmpty()) {
            item {
                VitalSenseCard {
                    Text(
                        text = "No prescriptions found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlumeTextSecondary
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
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = "${rx.doctorSpecialty} · ${rx.dateFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }
                            if (rx.isOcrExtracted) {
                                Surface(shape = PillShape, color = GlumeSuccessContainer) {
                                    Text(
                                        text = "AI Scanned",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GlumeSuccessText),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = GlumeBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Prescribed Medicines:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
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
                                        color = GlumeTextPrimary
                                    )
                                    Text(
                                        text = "${med.frequency} · ${med.duration}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GlumeTextSecondary
                                    )
                                    if (med.hasAlternativeAvailable) {
                                        Text(
                                            text = "💡 Doctor suggested alternative available",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeAlertCoral
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { selectedMedicineForNearby = med },
                                    modifier = Modifier.height(34.dp),
                                    shape = PillShape,
                                    border = BorderStroke(1.dp, NagarSevaPrimary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "📍 Find nearby",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NagarSevaPrimary
                                    )
                                }
                            }
                        }

                        if (rx.instructions.isNotBlank()) {
                            Text(
                                text = "Instructions: ${rx.instructions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
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
}