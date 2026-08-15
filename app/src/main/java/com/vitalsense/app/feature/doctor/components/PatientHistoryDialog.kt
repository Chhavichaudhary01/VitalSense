package com.vitalsense.app.feature.doctor.components

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
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.SeverityBadge
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientHistoryDialog(
    patient: Patient,
    conditions: List<ConditionRecord>,
    prescriptions: List<Prescription>,
    appointments: List<Appointment> = emptyList(),
    onDismiss: () -> Unit
) {
    val patientConditions = conditions.filter { it.patientId == patient.id }
    val patientPrescriptions = prescriptions.filter { it.patientId == patient.id }
    val patientAppointments = appointments.filter { it.patientId == patient.id }

    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

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
                // 1. Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📋 Medical History & Records",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Patient: ${patient.name} (${patient.age}y / ${patient.gender})",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text(text = "✕", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextSecondary)
                    }
                }

                HorizontalDivider(color = GlumeBorder)

                // 2. Patient Demographics & Health Profile Card
                VitalSenseCard(
                    backgroundColor = GlumeSurfaceElevated,
                    border = BorderStroke(1.dp, GlumeBorder)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Village: ${patient.villageName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            SeverityBadge(severity = patient.currentRiskLevel)
                        }

                        Text(
                            text = "Last Condition: ${patient.lastCondition} · Last Visit: ${patient.lastVisitDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                        Text(
                            text = "Assigned ASHA: ${patient.ashaWorkerName} · Emergency: ${patient.emergencyContact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumePrimaryPurpleLight
                        )
                    }
                }

                // 3. Past Conditions Log
                Text(
                    text = "Condition Submissions (${patientConditions.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )

                if (patientConditions.isEmpty()) {
                    Text(
                        text = "No condition records logged for this patient yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )
                } else {
                    patientConditions.forEach { record ->
                        VitalSenseCard {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${record.category.displayName} (${dateFormat.format(Date(record.timestamp))})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                    SeverityBadge(severity = record.severity)
                                }

                                Text(
                                    text = "Symptoms: ${record.notes}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GlumeTextPrimary
                                )

                                if (record.doctorResponse != null) {
                                    Surface(
                                        color = GlumePrimaryPurpleContainer,
                                        shape = CardShape,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(Spacing.sm)) {
                                            Text(
                                                text = "Doctor Advice: ${record.doctorResponse}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = GlumePrimaryPurpleLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Past Prescriptions Log
                Text(
                    text = "Prescriptions on File (${patientPrescriptions.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )

                if (patientPrescriptions.isEmpty()) {
                    Text(
                        text = "No prior prescriptions uploaded or issued.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )
                } else {
                    patientPrescriptions.forEach { rx ->
                        VitalSenseCard {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "By ${rx.doctorName} (${rx.dateFormatted})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                    if (rx.isOcrExtracted) {
                                        Surface(shape = PillShape, color = GlumeSuccessContainer) {
                                            Text(
                                                text = "AI Digitized",
                                                style = MaterialTheme.typography.labelSmall.copy(color = GlumeSuccessText, fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                rx.medicines.forEach { med ->
                                    Text(
                                        text = "• ${med.name} (${med.dosage}) - ${med.frequency} for ${med.duration}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GlumeTextPrimary
                                    )
                                }

                                if (rx.instructions.isNotBlank()) {
                                    Text(
                                        text = "Note: ${rx.instructions}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GlumeTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Past / Scheduled Appointments
                if (patientAppointments.isNotEmpty()) {
                    Text(
                        text = "Appointments History (${patientAppointments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )

                    patientAppointments.forEach { appt ->
                        VitalSenseCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${appt.dateFormatted} at ${appt.timeSlot}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                    Text(
                                        text = "Status: ${appt.status}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GlumeTextSecondary
                                    )
                                }
                                Surface(
                                    shape = PillShape,
                                    color = if (appt.status.contains("Pending", true)) GlumeWarningContainer else GlumeSuccessContainer
                                ) {
                                    Text(
                                        text = appt.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (appt.status.contains("Pending", true)) GlumeWarningAmber else GlumeSuccessText
                                        ),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Close Button
                VitalSenseButton(
                    text = "Close Medical History",
                    onClick = onDismiss,
                    style = com.vitalsense.app.core.ui.components.ButtonStyle.PRIMARY
                )
            }
        }
    }
}
