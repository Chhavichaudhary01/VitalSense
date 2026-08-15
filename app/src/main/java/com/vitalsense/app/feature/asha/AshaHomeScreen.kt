package com.vitalsense.app.feature.asha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

@Composable
fun AshaHomeScreen(
    asha: AshaWorker,
    patients: List<Patient>,
    notices: List<BroadcastNotice>,
    onSelectProxyPatient: (Patient) -> Unit,
    onRegisterPatientClick: () -> Unit = {},
    onSendNoticeClick: () -> Unit = {},
    onSavePrescription: (Prescription) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var ocrTargetPatient by remember { mutableStateOf<Patient?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Header with Greeting & ASHA ID Card
        item {
            Column {
                Text(
                    text = "Namaste, ${asha.name}",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "Assigned Villages: ${asha.assignedVillages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 2. ASHA Unique ID Card
        item {
            VitalSenseCard(
                backgroundColor = LavenderSecondary.copy(alpha = 0.45f),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "UNIQUE ASHA HELPER ID",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = asha.ashaUniqueId,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Share this ID with patients to link you as helper",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🆔", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

        // 3. Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                VitalSenseButton(
                    text = "+ New Patient",
                    onClick = onRegisterPatientClick,
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.DARK
                )
                VitalSenseButton(
                    text = "📢 Send Notice",
                    onClick = onSendNoticeClick,
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.SECONDARY
                )
            }
        }

        // 4. Caseload Summary Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Village Caseload (${patients.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
                if (asha.alertCount > 0) {
                    Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.25f)) {
                        Text(
                            text = "${asha.alertCount} High Risk",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CoralAlertDark),
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                        )
                    }
                }
            }
        }

        // 5. Patient Caseload Cards
        if (patients.isEmpty()) {
            item {
                VitalSenseCard(backgroundColor = SurfaceWhite) {
                    Column(
                        modifier = Modifier.padding(Spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No patients registered yet in this village.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            items(patients) { patient ->
                VitalSenseCard {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        // Patient name & Risk badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = patient.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Age: ${patient.age} (${patient.gender}) · ${patient.villageName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                            SeverityBadge(severity = patient.currentRiskLevel)
                        }

                        // Last condition & Next visit
                        Text(
                            text = "Condition: ${patient.lastCondition}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimaryNearBlack
                        )

                        Text(
                            text = "Last Visit: ${patient.lastVisitDate} · Next: ${patient.nextAppointmentDate ?: "None Scheduled"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )

                        HorizontalDivider(color = DividerSubtle, thickness = 1.dp)

                        // Action Buttons: Proxy Mode & Scan Rx (Accessible heights)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            OutlinedButton(
                                onClick = { ocrTargetPatient = patient },
                                modifier = Modifier.weight(1f).defaultMinSize(minHeight = 40.dp),
                                shape = PillShape,
                                border = BorderStroke(1.dp, CardBorderColor)
                            ) {
                                Text(text = "📷 Scan Rx", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = { onSelectProxyPatient(patient) },
                                modifier = Modifier.weight(1.3f).defaultMinSize(minHeight = 40.dp),
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkCharcoal,
                                    contentColor = LimePrimary
                                )
                            ) {
                                Text(text = "🤝 Proxy Mode", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        val emergencySosAlerts = notices.filter { it.isUrgent && it.senderRole == UserRole.PATIENT }
        val adminAdvisories = notices.filter { it.senderRole == UserRole.ADMIN }

        // 6. Emergency Patient SOS Alerts
        if (emergencySosAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "🚨 Emergency Patient SOS Alerts (${emergencySosAlerts.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CoralAlertDark
                )
            }

            items(emergencySosAlerts) { sos ->
                VitalSenseCard(
                    backgroundColor = CoralAlert.copy(alpha = 0.12f),
                    elevation = 2.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sos.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = CoralAlertDark
                            )
                            Surface(shape = PillShape, color = CoralAlert) {
                                Text(
                                    text = "HIGH PRIORITY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SurfaceWhite),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                )
                            }
                        }
                        Text(
                            text = sos.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "From: ${sos.senderName} · Village: ${sos.targetVillage ?: "General"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 7. District Health Advisories (Admin Broadcasts)
        if (adminAdvisories.isNotEmpty()) {
            item {
                Text(
                    text = "📢 District Health Advisories",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
            }

            items(adminAdvisories) { notice ->
                VitalSenseCard(
                    backgroundColor = if (notice.isUrgent) CoralAlert.copy(alpha = 0.12f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (notice.isUrgent) CoralAlertDark else TextPrimaryNearBlack
                        )
                        Text(
                            text = notice.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Issued by: ${notice.senderName} (${notice.senderRole.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }
    }

    ocrTargetPatient?.let { targetPatient ->
        com.vitalsense.app.feature.prescriptions.PrescriptionUploadDialog(
            patient = targetPatient,
            isAshaProxy = true,
            onDismiss = { ocrTargetPatient = null },
            onSavePrescription = onSavePrescription
        )
    }
}
