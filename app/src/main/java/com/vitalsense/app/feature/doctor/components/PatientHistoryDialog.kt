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
                // 1. Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📋 Medical History",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Patient: ${patient.name} (${patient.age}y / ${patient.gender})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text(text = "✕", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondaryMuted)
                    }
                }

                HorizontalDivider(color = DividerSubtle)

                // 2. Patient Demographics & Health Profile Card
                VitalSenseCard(backgroundColor = SurfaceWhite) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Village: ${patient.villageName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            SeverityBadge(severity = patient.currentRiskLevel)
                        }

                        Text(
                            text = "Last Condition: ${patient.lastCondition} · Last Visit: ${patient.lastVisitDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack
                        )

                        Text(
                            text = "Assigned ASHA: ${patient.ashaWorkerName} · Emergency Contact: ${patient.emergencyContact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                // 3. Past Prescriptions Section
                Text(
                    text = "💊 Past Prescriptions (${patientPrescriptions.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )

                if (patientPrescriptions.isEmpty()) {
                    VitalSenseCard(backgroundColor = SurfaceWhite) {
                        Text(
                            text = "No prior prescription history recorded for this patient.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                } else {
                    patientPrescriptions.forEach { rx ->
                        VitalSenseCard {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Prescribed by ${rx.doctorName}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${rx.doctorSpecialty} · ${rx.dateFormatted}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                    if (rx.isOcrExtracted) {
                                        Surface(shape = PillShape, color = SoftMintSuccess.copy(alpha = 0.5f)) {
                                            Text(
                                                text = "OCR Digitized",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SoftMintText),
                                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                            )
                                        }
                                    }
                                }

                                rx.medicines.forEach { med ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "• ${med.name} (${med.dosage})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                        Text(text = "${med.frequency} · ${med.duration}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryMuted)
                                    }
                                }

                                if (rx.instructions.isNotBlank()) {
                                    Text(
                                        text = "Instructions: ${rx.instructions}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Past Condition & Symptom Log History
                Text(
                    text = "🩺 Past Symptom Records (${patientConditions.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )

                if (patientConditions.isEmpty()) {
                    VitalSenseCard(backgroundColor = SurfaceWhite) {
                        Text(
                            text = "No condition records found.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                } else {
                    patientConditions.forEach { cond ->
                        VitalSenseCard {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = cond.category.displayName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = dateFormat.format(Date(cond.timestamp)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                    SeverityBadge(severity = cond.severity)
                                }

                                Text(
                                    text = "Symptoms: ${cond.notes}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimaryNearBlack
                                )

                                if (cond.ashaProxyLogged) {
                                    Surface(shape = PillShape, color = LavenderSecondary.copy(alpha = 0.4f)) {
                                        Text(
                                            text = "🤝 Logged via ASHA Proxy",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                        )
                                    }
                                }

                                if (cond.doctorResponse != null) {
                                    Surface(
                                        shape = CardShape,
                                        color = SoftMintSuccess.copy(alpha = 0.25f),
                                        border = BorderStroke(1.dp, SoftMintSuccess),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(Spacing.xs)) {
                                            Text(
                                                text = "Doctor Response (${cond.doctorResponseDoctorName ?: "Physician"}):",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = cond.doctorResponse,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimaryNearBlack
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Past & Upcoming Consultations
                if (patientAppointments.isNotEmpty()) {
                    Text(
                        text = "📅 Consultation Schedule (${patientAppointments.size})",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimaryNearBlack
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
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "With ${appt.doctorName} (${appt.doctorSpecialty})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                                Surface(shape = PillShape, color = SurfaceCream, border = BorderStroke(1.dp, CardBorderColor)) {
                                    Text(
                                        text = appt.status,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxs))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal, contentColor = LimePrimary)
                ) {
                    Text("Close History View", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
