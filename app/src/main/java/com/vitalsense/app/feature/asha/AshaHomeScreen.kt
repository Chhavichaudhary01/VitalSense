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
    val strings = LocalAppStrings.current
    var ocrTargetPatient by remember { mutableStateOf<Patient?>(null) }

    val totalPatients = patients.size
    val highRiskPatients = patients.count { it.currentRiskLevel == SeverityLevel.HIGH || it.currentRiskLevel == SeverityLevel.SEVERE }
    val visitedPatients = patients.count { it.lastVisitDate.isNotBlank() && it.lastVisitDate != "Never" }
    val followUpFraction = if (totalPatients > 0) visitedPatients.toFloat() / totalPatients else 1.0f

    val emergencySosAlerts = notices.filter { it.isUrgent && it.senderRole == UserRole.PATIENT }
    val adminAdvisories = notices.filter { it.senderRole == UserRole.ADMIN }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlumeBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Header with Glume Greeting
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = "Hi, ${asha.name}!",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = GlumeTextPrimary
                )
                Text(
                    text = "${strings.assignedVillages} ${asha.assignedVillages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GlumeTextSecondary
                )
            }
        }

        // 2. Glume Hero Completion Ring Card: Community Care Progress
        item {
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(
                            text = "COMMUNITY CASELOAD PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = GlumeTextSecondary
                        )
                        Text(
                            text = "$visitedPatients of $totalPatients Patients Monitored",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = if (highRiskPatients > 0) "$highRiskPatients patients need home checkup" else "All village caseloads stable",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (highRiskPatients > 0) GlumeAlertCoral else GlumeSuccessText
                        )
                    }

                    GlumeProgressRing(
                        progressFraction = followUpFraction,
                        size = 72.dp,
                        strokeWidth = 7.dp,
                        ringColor = GlumePrimaryPurple,
                        trackColor = GlumeSurfaceElevated
                    )
                }
            }
        }

        // 3. Glume Stat Display Pattern (3-Column Grid)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                GlumeStatCard(
                    label = "Caseload",
                    value = "$totalPatients",
                    icon = "👥",
                    modifier = Modifier.weight(1f),
                    badgeText = "Total",
                    badgeColor = GlumePrimaryPurple
                )
                GlumeStatCard(
                    label = "High Risk",
                    value = "$highRiskPatients",
                    icon = "⚠️",
                    modifier = Modifier.weight(1f),
                    badgeText = if (highRiskPatients > 0) "Alert" else "None",
                    badgeColor = if (highRiskPatients > 0) GlumeAlertCoral else GlumeSuccessMint
                )
                GlumeStatCard(
                    label = "Villages",
                    value = "${asha.assignedVillages.size}",
                    icon = "🏡",
                    modifier = Modifier.weight(1f),
                    badgeText = "Active",
                    badgeColor = GlumeSuccessMint
                )
            }
        }

        // 4. ASHA Unique ID Card (Glume Dark Elevated Style)
        item {
            VitalSenseCard(
                backgroundColor = GlumeSurfaceElevated,
                border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = strings.uniqueAshaCardTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = GlumePrimaryPurpleLight
                        )
                        Text(
                            text = asha.ashaUniqueId,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = strings.shareAshaIdDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GlumePrimaryPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🆔", fontSize = 22.sp)
                    }
                }
            }
        }

        // 5. Quick Action Buttons (Glume Primary & Secondary Pill Buttons)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                VitalSenseButton(
                    text = strings.newPatient,
                    onClick = onRegisterPatientClick,
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.PRIMARY
                )
                VitalSenseButton(
                    text = strings.sendNotice,
                    onClick = onSendNoticeClick,
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.DARK
                )
            }
        }

        // 6. Caseload Summary Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.villageCaseload} (${patients.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GlumeTextPrimary
                )
                if (asha.alertCount > 0) {
                    Surface(shape = PillShape, color = GlumeAlertContainer) {
                        Text(
                            text = "${asha.alertCount} ${strings.highRisk}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GlumeAlertCoral
                            ),
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // 7. Patient Caseload Cards (Glume Dark Slate Card Style)
        if (patients.isEmpty()) {
            item {
                VitalSenseCard {
                    Column(
                        modifier = Modifier.padding(Spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.noPatientsYet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlumeTextSecondary
                        )
                    }
                }
            }
        } else {
            items(patients) { patient ->
                val isHighRisk = patient.currentRiskLevel == SeverityLevel.HIGH || patient.currentRiskLevel == SeverityLevel.SEVERE

                VitalSenseCard(
                    backgroundColor = if (isHighRisk) GlumeAlertContainer else GlumeSurfaceCard,
                    border = BorderStroke(1.dp, if (isHighRisk) GlumeAlertCoral.copy(alpha = 0.4f) else GlumeBorder)
                ) {
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
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = "Age: ${patient.age} (${patient.gender}) · ${patient.villageName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }
                            SeverityBadge(severity = patient.currentRiskLevel)
                        }

                        // Last condition & Next visit
                        Text(
                            text = "Condition: ${patient.lastCondition}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = GlumeTextPrimary
                        )

                        Text(
                            text = "Last Visit: ${patient.lastVisitDate} · Next: ${patient.nextAppointmentDate ?: strings.noneScheduled}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )

                        HorizontalDivider(color = GlumeBorder, thickness = 1.dp)

                        // Action Buttons: Proxy Mode & Scan Rx
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            OutlinedButton(
                                onClick = { ocrTargetPatient = patient },
                                modifier = Modifier.weight(1f).defaultMinSize(minHeight = 40.dp),
                                shape = PillShape,
                                border = BorderStroke(1.dp, GlumeBorder)
                            ) {
                                Text(text = strings.scanRx, style = MaterialTheme.typography.labelSmall, color = GlumeTextPrimary)
                            }

                            Button(
                                onClick = { onSelectProxyPatient(patient) },
                                modifier = Modifier.weight(1.3f).defaultMinSize(minHeight = 40.dp),
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlumePrimaryPurple,
                                    contentColor = GlumeTextPrimary
                                )
                            ) {
                                Text(
                                    text = strings.proxyMode,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Emergency Patient SOS Alerts (With Subtle Coral Glow)
        if (emergencySosAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "${strings.emergencyPatientAlerts} (${emergencySosAlerts.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GlumeAlertCoral
                )
            }

            items(emergencySosAlerts) { sos ->
                VitalSenseCard(
                    backgroundColor = GlumeAlertContainer,
                    border = BorderStroke(1.dp, GlumeAlertCoral.copy(alpha = 0.5f))
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
                                color = GlumeAlertText
                            )
                            Surface(shape = PillShape, color = GlumeAlertCoral) {
                                Text(
                                    text = strings.highPriority,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GlumeTextPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = sos.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "From: ${sos.senderName} · Village: ${sos.targetVillage ?: "General"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                }
            }
        }

        // 9. District Health Advisories
        if (adminAdvisories.isNotEmpty()) {
            item {
                Text(
                    text = strings.districtAdvisories,
                    style = MaterialTheme.typography.headlineMedium,
                    color = GlumeTextPrimary
                )
            }

            items(adminAdvisories) { notice ->
                VitalSenseCard {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (notice.isUrgent) GlumeAlertText else GlumeTextPrimary
                        )
                        Text(
                            text = notice.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "${strings.issuedBy} ${notice.senderName} (${notice.senderRole.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
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
