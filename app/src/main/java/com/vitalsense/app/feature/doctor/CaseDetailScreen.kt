package com.vitalsense.app.feature.doctor

import androidx.compose.animation.*
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
import com.vitalsense.app.feature.doctor.components.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaseDetailScreen(
    record: ConditionRecord,
    patient: Patient?,
    priorPrescriptions: List<Prescription>,
    dispensaryStock: List<DispensaryItem>,
    currentDoctor: Doctor,
    allConditions: List<ConditionRecord> = emptyList(),
    allAppointments: List<Appointment> = emptyList(),
    onBack: () -> Unit,
    onSubmitResponse: (responseText: String, privateNotes: String?) -> Unit,
    onIssuePrescription: (medicines: List<PrescribedMedicine>, instructions: String) -> Unit,
    onProposeAppointment: (date: String, timeSlot: String) -> Unit,
    onReferCase: (targetSpecialty: DoctorSpecialty, referralNotes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var responseText by remember(record) { mutableStateOf(record.doctorResponse ?: "") }
    var privateNotes by remember(record) { mutableStateOf(record.privateDoctorNotes ?: "") }
    var showPrivateNotes by remember { mutableStateOf(record.privateDoctorNotes?.isNotBlank() == true) }

    var showHealthCardDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showPrescriptionDialog by remember { mutableStateOf(false) }
    var showAppointmentDialog by remember { mutableStateOf(false) }
    var showReferDialog by remember { mutableStateOf(false) }

    val isMentalHealthCase = record.category == ConditionCategory.MENTAL_HEALTH ||
            record.requestedDoctorType == DoctorSpecialty.PSYCHOLOGIST

    val quickReplies = listOf(
        "💧 Rest, hydration & light fluids for 3 days",
        "🌡️ If fever exceeds 102°F, report to PHC",
        "💊 Continue antibiotics for full course",
        "🥗 High iron diet with green leafy vegetables",
        "🧘 Practice daily 4-7-8 breathing exercises"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Header & Back Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = PillShape,
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 1.dp,
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "←", style = MaterialTheme.typography.labelLarge)
                        Text(text = "Case Queue", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Surface(
                    shape = PillShape,
                    color = Color(record.status.colorHex).copy(alpha = 0.35f)
                ) {
                    Text(
                        text = record.status.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
                        color = TextPrimaryNearBlack
                    )
                }
            }
        }

        // 2. Patient & Condition Summary Card
        item {
            VitalSenseCard(backgroundColor = SurfaceWhite, elevation = 2.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.patientName,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Village: ${record.villageName} · Category: ${record.category.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                        SeverityBadge(severity = record.severity)
                    }

                    HorizontalDivider(color = DividerSubtle)

                    Text(
                        text = "Reported Symptoms & Notes:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondaryMuted
                    )
                    Text(
                        text = record.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimaryNearBlack
                    )

                    HorizontalDivider(color = DividerSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (record.ashaProxyLogged) {
                            Surface(shape = PillShape, color = LavenderSecondary.copy(alpha = 0.45f)) {
                                Text(
                                    text = "🤝 Submitted via ASHA Helper",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                )
                            }
                        } else {
                            Text(
                                text = "Direct Patient Submission",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                        }

                        // View-Only Health Card & Past Records Triggers
                        patient?.let { _ ->
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                OutlinedButton(
                                    onClick = { showHistoryDialog = true },
                                    shape = PillShape,
                                    border = BorderStroke(1.dp, CardBorderColor),
                                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                                    modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                                ) {
                                    Text(text = "📋 History & Rx", style = MaterialTheme.typography.labelSmall)
                                }

                                OutlinedButton(
                                    onClick = { showHealthCardDialog = true },
                                    shape = PillShape,
                                    border = BorderStroke(1.dp, CardBorderColor),
                                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                                    modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                                ) {
                                    Text(text = "🪪 Health Card", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Mental Health Origin Flag
        if (isMentalHealthCase) {
            item {
                Surface(
                    color = LavenderSecondary.copy(alpha = 0.35f),
                    shape = CardShape,
                    border = BorderStroke(1.dp, LavenderSecondary.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(text = "🧠", style = MaterialTheme.typography.titleLarge)
                        Column {
                            Text(
                                text = "Mental Health Case Flag",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryNearBlack
                            )
                            Text(
                                text = "Patient logged psychological stress/anxiety symptoms. Approached with empathy and holistic care.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryNearBlack
                            )
                        }
                    }
                }
            }
        }

        // 4. Clinical Referral Badge (If already referred)
        if (record.referredByDoctorId != null) {
            item {
                VitalSenseCard(backgroundColor = BlushPinkTertiary.copy(alpha = 0.35f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "↗ Referred by Dr. ${record.referredByDoctorName ?: "Colleague"}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                        if (!record.referralNotes.isNullOrBlank()) {
                            Text(
                                text = "Referral Notes: ${record.referralNotes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryNearBlack
                            )
                        }
                    }
                }
            }
        }

        // 5. Section: Doctor Medical Response Composer
        item {
            Text(
                text = "Clinical Response & Advice",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimaryNearBlack
            )
        }

        // 5.1 Quick Reply Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = "Quick Template Responses:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    quickReplies.forEach { reply ->
                        Surface(
                            onClick = {
                                responseText = if (responseText.isBlank()) reply else "$responseText\n$reply"
                            },
                            shape = PillShape,
                            color = SurfaceWhite,
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text(
                                text = reply,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimaryNearBlack,
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                            )
                        }
                    }
                }
            }
        }

        // 5.2 Response Text Input Box
        item {
            VitalSenseCard(backgroundColor = SurfaceWhite) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    VitalSenseTextField(
                        value = responseText,
                        onValueChange = { responseText = it },
                        label = "Medical Advice & Treatment Plan",
                        placeholder = "Type clear, vernacular medical guidance for patient and ASHA worker...",
                        singleLine = false,
                        maxLines = 6
                    )

                    // Private Doctor Notes Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔒 Confidential Clinical Notes (Doctor-Only)",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondaryMuted
                        )
                        Switch(
                            checked = showPrivateNotes,
                            onCheckedChange = { showPrivateNotes = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkCharcoal,
                                checkedTrackColor = LimePrimary
                            )
                        )
                    }

                    if (showPrivateNotes) {
                        VitalSenseTextField(
                            value = privateNotes,
                            onValueChange = { privateNotes = it },
                            label = "Private Internal Notes",
                            placeholder = "Diagnostic reasoning, differential diagnoses, internal follow-up observations...",
                            singleLine = false,
                            maxLines = 3
                        )
                    }

                    // Submit Response Button
                    VitalSenseButton(
                        text = if (record.doctorResponse != null) "Update Clinical Response ✓" else "Submit Medical Advice ✓",
                        onClick = {
                            onSubmitResponse(
                                responseText.trim(),
                                if (showPrivateNotes && privateNotes.isNotBlank()) privateNotes.trim() else null
                            )
                        },
                        style = ButtonStyle.PRIMARY,
                        enabled = responseText.isNotBlank()
                    )
                }
            }
        }

        // 6. Clinical Tool Actions
        item {
            Text(
                text = "Clinical Actions",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimaryNearBlack
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                VitalSenseButton(
                    text = "💊 Issue Rx",
                    onClick = { showPrescriptionDialog = true },
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.DARK
                )
                VitalSenseButton(
                    text = "📅 Propose Appt",
                    onClick = { showAppointmentDialog = true },
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.SECONDARY
                )
                VitalSenseButton(
                    text = "↗ Refer",
                    onClick = { showReferDialog = true },
                    modifier = Modifier.weight(0.9f),
                    style = ButtonStyle.OUTLINED
                )
            }
        }

        // 7. Prior Prescriptions on Record
        if (priorPrescriptions.isNotEmpty()) {
            item {
                Text(
                    text = "Patient's Active Prescriptions (${priorPrescriptions.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
            }

            items(priorPrescriptions) { rx ->
                VitalSenseCard {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Issued by ${rx.doctorName} (${rx.doctorSpecialty})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Date: ${rx.dateFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                            if (rx.isOcrExtracted) {
                                Surface(shape = PillShape, color = SoftMintSuccess.copy(alpha = 0.5f)) {
                                    Text(
                                        text = "OCR Digitized",
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
                                text = "Instructions: ${rx.instructions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showHistoryDialog && patient != null) {
        PatientHistoryDialog(
            patient = patient,
            conditions = allConditions,
            prescriptions = priorPrescriptions,
            appointments = allAppointments,
            onDismiss = { showHistoryDialog = false }
        )
    }

    if (showHealthCardDialog && patient != null) {
        PatientHealthCardViewOnlyDialog(
            patient = patient,
            onDismiss = { showHealthCardDialog = false }
        )
    }

    if (showPrescriptionDialog) {
        PrescriptionComposerDialog(
            patient = patient,
            patientNameFallback = record.patientName,
            caseId = record.id,
            dispensaryStock = dispensaryStock,
            onDismiss = { showPrescriptionDialog = false },
            onIssuePrescription = onIssuePrescription
        )
    }

    if (showAppointmentDialog) {
        ProposeAppointmentDialog(
            patient = patient,
            patientNameFallback = record.patientName,
            onDismiss = { showAppointmentDialog = false },
            onPropose = onProposeAppointment
        )
    }

    if (showReferDialog) {
        ReferCaseDialog(
            patientName = record.patientName,
            currentSpecialty = currentDoctor.specialty,
            onDismiss = { showReferDialog = false },
            onRefer = onReferCase
        )
    }
}
