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
import com.vitalsense.app.feature.asha.components.RegisterPatientDialog
import com.vitalsense.app.feature.asha.components.SendNoticeDialog
import com.vitalsense.app.core.util.DismissedNoticeHelper
import com.vitalsense.app.core.util.AudioGuidanceHelper
import kotlinx.coroutines.launch

@Composable
fun AshaHomeScreen(
    asha: AshaWorker,
    patients: List<Patient>,
    notices: List<BroadcastNotice>,
    onSelectProxyPatient: (Patient) -> Unit,
    onRegisterPatientClick: () -> Unit = {},
    onSendNoticeClick: () -> Unit = {},
    onSavePatient: (Patient) -> Unit = {},
    onSendNotice: (BroadcastNotice) -> Unit = {},
    onSavePrescription: (Prescription) -> Unit = {},
    onTriggerSosForPatient: suspend (Patient) -> Boolean = { true },
    onImmunizationClick: () -> Unit = {},
    onDailyRoundsClick: () -> Unit = {},
    onMedicineRestockClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    var ocrTargetPatient by remember { mutableStateOf<Patient?>(null) }

    var showRegisterPatientDialog by remember { mutableStateOf(false) }
    var showSendNoticeDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Per-patient Emergency SOS state management
    var sosConfirmationPatient by remember { mutableStateOf<Patient?>(null) }
    var loadingSosPatientId by remember { mutableStateOf<String?>(null) }
    var sosFailedPatient by remember { mutableStateOf<Patient?>(null) }

    val totalPatients = patients.size
    val highRiskPatients = patients.count { it.currentRiskLevel == SeverityLevel.HIGH || it.currentRiskLevel == SeverityLevel.SEVERE }
    val visitedPatients = patients.count { it.lastVisitDate.isNotBlank() && it.lastVisitDate != "Never" }
    val followUpFraction = if (totalPatients > 0) visitedPatients.toFloat() / totalPatients else 1.0f

    val context = androidx.compose.ui.platform.LocalContext.current

    var clearedSosIds by remember { mutableStateOf(DismissedNoticeHelper.getClearedSosIds(context)) }
    var dismissedAdvisoryIds by remember { mutableStateOf(DismissedNoticeHelper.getDismissedAdvisoryIds(context, "asha")) }
    var sosToClear by remember { mutableStateOf<BroadcastNotice?>(null) }

    val emergencySosAlerts = remember(notices, clearedSosIds) {
        notices.filter { it.isUrgent && it.senderRole == UserRole.PATIENT && it.id !in clearedSosIds }
    }
    val adminAdvisories = remember(notices, dismissedAdvisoryIds) {
        notices.filter { it.senderRole == UserRole.ADMIN && it.id !in dismissedAdvisoryIds }
    }

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

        // Success banner
        if (successMessage != null) {
            item {
                Surface(
                    shape = PillShape,
                    color = GlumeSuccessContainer,
                    border = BorderStroke(1.dp, GlumeSuccessMint),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = successMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeSuccessMint
                        )
                        IconButton(onClick = { successMessage = null }, modifier = Modifier.size(24.dp)) {
                            Text("✕", color = GlumeSuccessMint, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // SOS Failure banner with Retry option
        if (sosFailedPatient != null) {
            item {
                val failedPatient = sosFailedPatient!!
                Surface(
                    shape = PillShape,
                    color = GlumeAlertContainer,
                    border = BorderStroke(1.dp, GlumeAlertCoral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("⚠️", fontSize = 14.sp)
                            Text(
                                text = "${strings.sosFailedForPatient} (${failedPatient.name})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumeAlertCoral
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = {
                                    val pToRetry = failedPatient
                                    sosFailedPatient = null
                                    loadingSosPatientId = pToRetry.id
                                    coroutineScope.launch {
                                        try {
                                            val success = onTriggerSosForPatient(pToRetry)
                                            loadingSosPatientId = null
                                            if (success) {
                                                successMessage = "✓ ${strings.sosDispatchedForPatient} ${pToRetry.name}!"
                                            } else {
                                                sosFailedPatient = pToRetry
                                            }
                                        } catch (e: Exception) {
                                            loadingSosPatientId = null
                                            sosFailedPatient = pToRetry
                                        }
                                    }
                                }
                            ) {
                                Text(strings.retry, color = GlumeAlertCoral, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            IconButton(onClick = { sosFailedPatient = null }, modifier = Modifier.size(24.dp)) {
                                Text("✕", color = GlumeAlertCoral, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 5. Quick Action Buttons (Glume Primary & Secondary Pill Buttons)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    VitalSenseButton(
                        text = strings.newPatient,
                        onClick = { showRegisterPatientDialog = true },
                        modifier = Modifier.weight(1f),
                        style = ButtonStyle.PRIMARY
                    )
                    VitalSenseButton(
                        text = strings.sendNotice,
                        onClick = { showSendNoticeDialog = true },
                        modifier = Modifier.weight(1f),
                        style = ButtonStyle.DARK
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    VitalSenseButton(
                        text = "Immunization Tracker",
                        onClick = onImmunizationClick,
                        modifier = Modifier.weight(1f),
                        style = ButtonStyle.SECONDARY
                    )
                    VitalSenseButton(
                        text = "Daily Rounds",
                        onClick = onDailyRoundsClick,
                        modifier = Modifier.weight(1f),
                        style = ButtonStyle.SECONDARY
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    VitalSenseButton(
                        text = "Medicine Restock",
                        onClick = onMedicineRestockClick,
                        modifier = Modifier.fillMaxWidth(),
                        style = ButtonStyle.SECONDARY
                    )
                }
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
                val isAssignedToThisAsha = patient.ashaWorkerId == asha.id
                val isSosInFlight = loadingSosPatientId == patient.id

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

                        // Action Buttons: Per-Patient Emergency SOS, Scan Rx, & Proxy Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Red Per-Patient Emergency SOS Action Button
                            Button(
                                onClick = { sosConfirmationPatient = patient },
                                enabled = isAssignedToThisAsha && !isSosInFlight,
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlumeAlertCoral,
                                    contentColor = GlumeTextPrimary,
                                    disabledContainerColor = GlumeAlertCoral.copy(alpha = 0.4f),
                                    disabledContentColor = GlumeTextPrimary.copy(alpha = 0.6f)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 40.dp, minWidth = 40.dp)
                            ) {
                                if (isSosInFlight) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = GlumeTextPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = "🚨", fontSize = 14.sp)
                                        Text(
                                            text = "SOS",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }

                            // 2. Scan Rx Button
                            OutlinedButton(
                                onClick = { ocrTargetPatient = patient },
                                modifier = Modifier.weight(1f).defaultMinSize(minHeight = 40.dp),
                                shape = PillShape,
                                border = BorderStroke(1.dp, GlumeBorder)
                            ) {
                                Text(text = strings.scanRx, style = MaterialTheme.typography.labelSmall, color = GlumeTextPrimary)
                            }

                            // 3. Proxy Mode Button
                            Button(
                                onClick = { onSelectProxyPatient(patient) },
                                modifier = Modifier.weight(1.2f).defaultMinSize(minHeight = 40.dp),
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "From: ${sos.senderName} · Village: ${sos.targetVillage ?: "General"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { sosToClear = sos },
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(containerColor = GlumeAlertCoral),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Mark Emergency Clear",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${strings.issuedBy} ${notice.senderName} (${notice.senderRole.name})",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = {
                                    DismissedNoticeHelper.dismissAdvisory(context, "asha", notice.id)
                                    dismissedAdvisoryIds = dismissedAdvisoryIds + notice.id
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = PillShape
                            ) {
                                Text(
                                    text = "✕ Dismiss",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NagarSevaPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Per-patient Emergency SOS Confirmation Dialog
    sosConfirmationPatient?.let { targetPatient ->
        VitalSenseDialog(
            onDismissRequest = { sosConfirmationPatient = null },
            title = strings.sosAlertForPatient,
            icon = { Text("🚨", fontSize = 22.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        val pToTrigger = targetPatient
                        sosConfirmationPatient = null
                        loadingSosPatientId = pToTrigger.id
                        coroutineScope.launch {
                            try {
                                val success = onTriggerSosForPatient(pToTrigger)
                                loadingSosPatientId = null
                                if (success) {
                                    successMessage = "✓ ${strings.sosDispatchedForPatient} ${pToTrigger.name}!"
                                } else {
                                    sosFailedPatient = pToTrigger
                                }
                            } catch (e: Exception) {
                                loadingSosPatientId = null
                                sosFailedPatient = pToTrigger
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlumeAlertCoral),
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(strings.yesSendAlert, color = GlumeTextPrimary, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { sosConfirmationPatient = null },
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(strings.cancel, color = GlumeTextSecondary, style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "${strings.confirmSosPatientMsg} ${targetPatient.name}?",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
                Text(
                    text = "• ${strings.village}: ${targetPatient.villageName}\n• Age: ${targetPatient.age} (${targetPatient.gender})\n• Emergency Contact: ${targetPatient.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlumeTextSecondary
                )
                Text(
                    text = "This will immediately dispatch a high-priority SOS alert to doctors and emergency response.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlumeAlertCoral
                )
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

    if (showRegisterPatientDialog) {
        RegisterPatientDialog(
            asha = asha,
            onDismiss = { showRegisterPatientDialog = false },
            onRegister = { newPatient ->
                onSavePatient(newPatient)
                showRegisterPatientDialog = false
                successMessage = "✓ Registered ${newPatient.name} into your caseload!"
            }
        )
    }

    if (showSendNoticeDialog) {
        SendNoticeDialog(
            asha = asha,
            onDismiss = { showSendNoticeDialog = false },
            onSend = { notice ->
                onSendNotice(notice)
                showSendNoticeDialog = false
                successMessage = "✓ Broadcast advisory sent to village!"
            }
        )
    }

    if (sosToClear != null) {
        AlertDialog(
            onDismissRequest = { sosToClear = null },
            title = {
                Text(
                    text = "Confirm Emergency Resolved",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure this emergency alert for ${sosToClear?.senderName} has been addressed and the patient is safe?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GlumeTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = sosToClear!!.id
                        DismissedNoticeHelper.clearSos(context, id)
                        clearedSosIds = clearedSosIds + id
                        sosToClear = null
                        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlumeSuccessMint),
                    shape = PillShape
                ) {
                    Text(
                        text = "Yes, Mark Clear & Dismiss",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { sosToClear = null },
                    shape = PillShape
                ) {
                    Text("Cancel", color = GlumeTextSecondary)
                }
            }
        )
    }
}
