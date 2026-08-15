package com.vitalsense.app.feature.patient

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
fun PatientHomeScreen(
    patient: Patient,
    notices: List<BroadcastNotice> = emptyList(),
    prescriptions: List<Prescription> = emptyList(),
    onCategoryClick: (ConditionCategory) -> Unit = {},
    onViewHealthCard: () -> Unit = {},
    onTriggerSos: () -> Unit = {},
    onSavePrescription: (Prescription) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showSosConfirmation by remember { mutableStateOf(false) }
    var sosSentSuccess by remember { mutableStateOf(false) }
    var showPrescriptionUploadDialog by remember { mutableStateOf(false) }

    val adminAdvisories = notices.filter {
        it.senderRole == UserRole.ADMIN || it.targetRole == "ALL" || it.targetRole == "PATIENT"
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Personalized Greeting
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${strings.namaste}, ${patient.name.split(" ").first()}",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "${strings.village}: ${patient.villageName} · ${strings.ashaAssigned}: ${patient.ashaWorkerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    SeverityBadge(severity = patient.currentRiskLevel)
                }
            }
        }

        // 2. Inline Dismissible Page Guide
        item {
            InlineHelpBanner(
                title = strings.patientGuideTitle,
                message = strings.patientGuideMsg
            )
        }

        // 3. Hero Card: Offline Health Card & Daily Status
        item {
            VitalSenseCard(
                backgroundColor = LimePrimary.copy(alpha = 0.85f),
                elevation = 2.dp,
                onClick = onViewHealthCard
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text(text = "🪪", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = strings.offlineHealthCard,
                                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.5.sp),
                                color = DarkCharcoal
                            )
                        }
                        Surface(
                            shape = PillShape,
                            color = DarkCharcoal
                        ) {
                            Text(
                                text = strings.viewCard,
                                style = MaterialTheme.typography.labelSmall.copy(color = LimePrimary, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                            )
                        }
                    }

                    Text(
                        text = "${strings.activeCondition} ${patient.lastCondition}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimaryNearBlack
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${strings.nextCheckup} ${patient.nextAppointmentDate ?: strings.noneScheduled}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack.copy(alpha = 0.8f)
                        )
                        Text(
                            text = strings.cachedOffline,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SoftMintText)
                        )
                    }
                }
            }
        }

        // 4. Section Title: Health Categories
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = strings.howCanWeHelp,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = strings.tapServiceDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 4.1 Categories 2-Column Grid
        item {
            val categories = listOf(
                ConditionCategory.GENERAL_MEDICINE,
                ConditionCategory.MATERNAL_HEALTH,
                ConditionCategory.FITNESS,
                ConditionCategory.NUTRITION,
                ConditionCategory.MENTAL_HEALTH
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                categories.chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        rowCategories.forEach { category ->
                            CategoryChip(
                                category = category,
                                isSelected = false,
                                onClick = { onCategoryClick(category) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 5. My Prescriptions & Medicines Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.myPrescriptions} (${prescriptions.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )

                Button(
                    onClick = { showPrescriptionUploadDialog = true },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = LimePrimary, contentColor = TextPrimaryNearBlack),
                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                    modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                ) {
                    Text(strings.uploadRx, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (prescriptions.isEmpty()) {
            item {
                VitalSenseCard(backgroundColor = SurfaceWhite) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.noPrescriptions,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = strings.scanOrWrite,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                        OutlinedButton(
                            onClick = { showPrescriptionUploadDialog = true },
                            shape = PillShape
                        ) {
                            Text(strings.uploadRx, style = MaterialTheme.typography.labelSmall)
                        }
                    }
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
                                        text = "AI Scanned",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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
                                Text(
                                    text = "• ${med.name} (${med.dosage})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${med.frequency} · ${med.duration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        if (rx.instructions.isNotBlank()) {
                            Text(
                                text = "Note: ${rx.instructions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                    }
                }
            }
        }

        // 6. District Health Advisories (Admin Broadcasts)
        if (adminAdvisories.isNotEmpty()) {
            item {
                Text(
                    text = strings.districtAdvisories,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
            }

            items(adminAdvisories) { advisory ->
                VitalSenseCard(
                    backgroundColor = if (advisory.isUrgent) CoralAlert.copy(alpha = 0.12f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = advisory.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (advisory.isUrgent) CoralAlertDark else TextPrimaryNearBlack
                            )
                            if (advisory.isUrgent) {
                                Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.25f)) {
                                    Text(
                                        text = strings.urgent,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CoralAlertDark),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                    )
                                }
                            }
                        }
                        Text(
                            text = advisory.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "${strings.issuedBy} ${advisory.senderName} (${advisory.senderRole.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 7. Persistent Emergency SOS Banner
        item {
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Surface(
                onClick = { showSosConfirmation = true },
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = CoralAlert,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚨", style = MaterialTheme.typography.titleMedium)
                        }
                        Column {
                            Text(
                                text = strings.emergencySos,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                            )
                            Text(
                                text = strings.emergencySosDesc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SurfaceWhite.copy(alpha = 0.95f)
                                )
                            )
                        }
                    }
                    Surface(
                        shape = PillShape,
                        color = SurfaceWhite
                    ) {
                        Text(
                            text = strings.trigger,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CoralAlertDark
                            ),
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                        )
                    }
                }
            }
        }
    }

    val sosMsg = "EMERGENCY SOS from ${patient.name} (${patient.villageName}, Age ${patient.age}). Contact: ${patient.phone}."

    // Custom Styled SOS Confirmation Dialog
    if (showSosConfirmation) {
        VitalSenseDialog(
            onDismissRequest = { showSosConfirmation = false },
            title = strings.confirmSosTitle,
            icon = { Text("🚨", style = MaterialTheme.typography.titleLarge) },
            confirmButton = {
                Button(
                    onClick = {
                        showSosConfirmation = false
                        onTriggerSos()
                        sosSentSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(strings.yesSendAlert, color = SurfaceWhite, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSosConfirmation = false },
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(strings.cancel, style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = strings.confirmSosMsg,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "• ${strings.ashaAssigned}: ${patient.ashaWorkerName}\n• Available Doctors\n• Emergency SMS: ${patient.emergencyContact}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimaryNearBlack
                )
            }
        }
    }

    // Custom Styled SOS Sent Dialog with 0-Internet Fallbacks
    if (sosSentSuccess) {
        VitalSenseDialog(
            onDismissRequest = { sosSentSuccess = false },
            title = strings.sosDispatchedTitle,
            icon = { Text("🚨", style = MaterialTheme.typography.titleLarge) },
            confirmButton = {
                Button(
                    onClick = { sosSentSuccess = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(strings.done, color = LimePrimary, style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = strings.sosDispatchedMsg,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(color = DividerSubtle)

                Text(
                    text = strings.zeroInternetFallbacks,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    OutlinedButton(
                        onClick = {
                            com.vitalsense.app.core.util.EmergencySosHelper.sendCellularSmsFallback(
                                context = context,
                                recipientPhone = patient.emergencyContact,
                                message = sosMsg
                            )
                        },
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp),
                        shape = PillShape
                    ) {
                        Text(strings.smsAsha, style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = {
                            com.vitalsense.app.core.util.EmergencySosHelper.dialEmergencyCall(
                                context = context,
                                phoneNumber = "108"
                            )
                        },
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                    ) {
                        Text(strings.call108, color = SurfaceWhite, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    if (showPrescriptionUploadDialog) {
        com.vitalsense.app.feature.prescriptions.PrescriptionUploadDialog(
            patient = patient,
            isAshaProxy = false,
            onDismiss = { showPrescriptionUploadDialog = false },
            onSavePrescription = onSavePrescription
        )
    }
}
