package com.vitalsense.app.feature.asha

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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // 1. Header with Greeting & ASHA ID Card
        item {
            Column {
                Text(
                    text = "Namaste, ${asha.name}",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "Assigned Villages: ${asha.assignedVillages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 2. ASHA Unique ID Card (PRD §3.2)
        item {
            VitalSenseCard(
                backgroundColor = LavenderSecondary.copy(alpha = 0.4f),
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
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = asha.ashaUniqueId,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Share this ID with patients to add you as helper",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
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
                        Text(text = "🆔", fontSize = 20.sp)
                    }
                }
            }
        }

        // 3. Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                    style = ButtonStyle.PRIMARY
                )
            }
        }

        // 4. Section Header: Active Caseload & Proxy Access
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Patient Caseload (${patients.size})",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "Tap 'Proxy' to act for patient",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 5. Patient Caseload Cards with Proxy Trigger
        items(patients) { patient ->
            VitalSenseCard(
                elevation = 2.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${patient.name} (${patient.gender}, ${patient.age}y)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimaryNearBlack
                            )
                            Text(
                                text = "Village: ${patient.villageName} · Ph: ${patient.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                        SeverityBadge(severity = patient.currentRiskLevel)
                    }

                    Text(
                        text = "Recent: ${patient.lastCondition}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPrimaryNearBlack
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next: ${patient.nextAppointmentDate ?: "None"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { ocrTargetPatient = patient },
                                shape = PillShape,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "📷 Scan Rx",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Button(
                                onClick = { onSelectProxyPatient(patient) },
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LimePrimary,
                                    contentColor = TextPrimaryNearBlack
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "🤝 Act as Proxy",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
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
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CoralAlert
                    )
                )
            }

            items(emergencySosAlerts) { sos ->
                VitalSenseCard(
                    backgroundColor = CoralAlert.copy(alpha = 0.15f),
                    elevation = 3.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sos.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CoralAlert
                                )
                            )
                            Surface(shape = PillShape, color = CoralAlert) {
                                Text(
                                    text = "HIGH PRIORITY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SurfaceWhite),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
                            style = MaterialTheme.typography.labelSmall,
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
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPrimaryNearBlack
                )
            }

            items(adminAdvisories) { notice ->
                VitalSenseCard(
                    backgroundColor = if (notice.isUrgent) CoralAlert.copy(alpha = 0.15f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (notice.isUrgent) CoralAlert else TextPrimaryNearBlack
                            )
                        )
                        Text(
                            text = notice.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Issued by: ${notice.senderName} (${notice.senderRole.name})",
                            style = MaterialTheme.typography.labelSmall,
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
            onSavePrescription = { rx ->
                onSavePrescription(rx)
            }
        )
    }
}

