package com.vitalsense.app.feature.doctor

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
import com.vitalsense.app.feature.doctor.components.PatientHistoryDialog
import com.vitalsense.app.feature.doctor.components.ScheduleAppointmentDialog

@Composable
fun DoctorHomeScreen(
    doctor: Doctor,
    cases: List<ConditionRecord>,
    appointments: List<Appointment>,
    dispensaryStock: List<DispensaryItem>,
    patients: List<Patient> = emptyList(),
    notices: List<BroadcastNotice> = emptyList(),
    allConditions: List<ConditionRecord> = emptyList(),
    allPrescriptions: List<Prescription> = emptyList(),
    onSelectCase: (ConditionRecord) -> Unit,
    onAcceptAppointment: (String) -> Unit = {},
    onDeclineAppointment: (String) -> Unit = {},
    onProposeAppointment: (patientId: String, patientName: String, date: String, timeSlot: String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedPatientForHistory by remember { mutableStateOf<Patient?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val pendingCases = cases.filter { it.status == CaseStatus.PENDING_REVIEW || it.status == CaseStatus.IN_PROGRESS }
    val severeCount = cases.count { it.severity == SeverityLevel.SEVERE || it.severity == SeverityLevel.HIGH }
    val lowStockCount = dispensaryStock.count { it.isLowStock }

    val emergencySosAlerts = notices.filter { it.isUrgent && it.senderRole == UserRole.PATIENT }
    val adminDirectives = notices.filter { it.senderRole == UserRole.ADMIN }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
    ) {
        // 1. Doctor Header
        item {
            Column {
                Text(
                    text = doctor.name,
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "${doctor.specialty.displayName} · ${doctor.hospitalName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 1.5 Emergency Patient SOS Alerts
        if (emergencySosAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "🚨 Patient Emergency SOS Alerts (${emergencySosAlerts.size})",
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
                            text = "Village: ${sos.targetVillage ?: "General"} · Patient Alert",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 2. Metrics summary KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Pending Cases KPI
                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = SurfaceWhite,
                    contentPadding = Spacing.sm
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "Pending",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = "${pendingCases.size}",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Active in Queue",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                // Severe / Critical Alert KPI
                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = if (severeCount > 0) CoralAlert.copy(alpha = 0.12f) else SurfaceWhite,
                    contentPadding = Spacing.sm
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "Critical Cases",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (severeCount > 0) CoralAlertDark else TextSecondaryMuted
                        )
                        Text(
                            text = "$severeCount",
                            style = MaterialTheme.typography.displayMedium,
                            color = if (severeCount > 0) CoralAlertDark else TextPrimaryNearBlack
                        )
                        Text(
                            text = if (severeCount > 0) "Immediate Triage" else "All Normal",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (severeCount > 0) CoralAlertDark else TextSecondaryMuted
                        )
                    }
                }

                // Scheduled Appointments KPI
                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = SurfaceWhite,
                    contentPadding = Spacing.sm
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "Appointments",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = "${appointments.size}",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Scheduled",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 3. Section: Specialist Triage Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Specialist Triage Queue (${cases.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "${doctor.specialty.displayName} Stream",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
        }

        if (cases.isEmpty()) {
            item {
                VitalSenseCard(backgroundColor = SurfaceWhite) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(text = "🎉", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "No pending patient cases in your triage queue.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                    }
                }
            }
        } else {
            items(cases) { record ->
                val isSevere = record.severity == SeverityLevel.SEVERE || record.severity == SeverityLevel.HIGH
                val isMentalHealth = record.category == ConditionCategory.MENTAL_HEALTH || record.requestedDoctorType == DoctorSpecialty.PSYCHOLOGIST

                VitalSenseCard(
                    backgroundColor = if (isSevere) CoralAlert.copy(alpha = 0.08f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = record.patientName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Village: ${record.villageName} · ${record.category.displayName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                            SeverityBadge(severity = record.severity)
                        }

                        if (isMentalHealth) {
                            Surface(shape = PillShape, color = LavenderSecondary.copy(alpha = 0.45f)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                                ) {
                                    Text(text = "🧠", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = "Mental Health Referral",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimaryNearBlack
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Symptoms: ${record.notes}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status Pill
                            Surface(
                                shape = PillShape,
                                color = Color(record.status.colorHex).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = record.status.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                                    color = TextPrimaryNearBlack
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                OutlinedButton(
                                    onClick = {
                                        selectedPatientForHistory = patients.find { it.id == record.patientId }
                                            ?: Patient(
                                                id = record.patientId,
                                                name = record.patientName,
                                                age = 30,
                                                gender = "Not specified",
                                                phone = "N/A",
                                                villageId = "vil_1",
                                                villageName = record.villageName,
                                                ashaWorkerId = "asha_1",
                                                ashaWorkerName = "ASHA Assigned",
                                                currentRiskLevel = record.severity,
                                                lastCondition = record.notes,
                                                lastVisitDate = "Recent",
                                                nextAppointmentDate = null,
                                                emergencyContact = "108"
                                            )
                                    },
                                    shape = PillShape,
                                    border = BorderStroke(1.dp, CardBorderColor),
                                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                                ) {
                                    Text(text = "📋 History", style = MaterialTheme.typography.labelSmall)
                                }

                                Button(
                                    onClick = { onSelectCase(record) },
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                                ) {
                                    Text(text = "Review →", color = LimePrimary, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Section: Upcoming Appointments
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scheduled Appointments (${appointments.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )

                Button(
                    onClick = { showScheduleDialog = true },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderSecondary, contentColor = TextPrimaryNearBlack),
                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                    modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                ) {
                    Text(text = "➕ Propose Appt", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (appointments.isEmpty()) {
            item {
                VitalSenseCard(backgroundColor = SurfaceWhite) {
                    Text(
                        text = "No appointments scheduled. Tap 'Propose Appt' to schedule a patient consultation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
            }
        } else {
            items(appointments) { appointment ->
                val isPending = appointment.status.contains("Pending", ignoreCase = true)
                VitalSenseCard(
                    backgroundColor = if (isPending) AmberWarning.copy(alpha = 0.15f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = appointment.patientName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${appointment.dateFormatted} at ${appointment.timeSlot}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                            Surface(
                                shape = PillShape,
                                color = if (isPending) AmberWarning else SoftMintSuccess
                            ) {
                                Text(
                                    text = appointment.status,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                                    color = TextPrimaryNearBlack
                                )
                            }
                        }

                        if (isPending) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.End),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { onDeclineAppointment(appointment.id) },
                                    shape = PillShape
                                ) {
                                    Text("Decline", color = CoralAlertDark, style = MaterialTheme.typography.labelSmall)
                                }
                                Button(
                                    onClick = { onAcceptAppointment(appointment.id) },
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftMintSuccess),
                                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                                    modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                                ) {
                                    Text("Accept ✓", color = SoftMintText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Section: Dispensary Stock Check
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dispensary Stock Check",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
                if (lowStockCount > 0) {
                    Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.25f)) {
                        Text(
                            text = "$lowStockCount LOW STOCK",
                            style = MaterialTheme.typography.labelSmall.copy(color = CoralAlertDark, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                        )
                    }
                }
            }
        }

        items(dispensaryStock) { item ->
            VitalSenseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.medicineName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(
                            text = "${item.availableQuantity} ${item.unit}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isLowStock) CoralAlertDark else TextPrimaryNearBlack
                            )
                        )
                        if (item.isLowStock) {
                            Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.25f)) {
                                Text(
                                    text = "LOW",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CoralAlertDark, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. District Health Directives (Admin Broadcasts)
        if (adminDirectives.isNotEmpty()) {
            item {
                Text(
                    text = "📢 District Health Directives",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
            }

            items(adminDirectives) { directive ->
                VitalSenseCard(
                    backgroundColor = if (directive.isUrgent) CoralAlert.copy(alpha = 0.12f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = directive.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (directive.isUrgent) CoralAlertDark else TextPrimaryNearBlack
                            )
                            if (directive.isUrgent) {
                                Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.25f)) {
                                    Text(
                                        text = "DIRECTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CoralAlertDark),
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                                    )
                                }
                            }
                        }
                        Text(
                            text = directive.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "From: ${directive.senderName} (${directive.senderRole.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 7. Patient Medical History & Prescriptions Directory
        if (patients.isNotEmpty()) {
            item {
                Text(
                    text = "🔍 Patient Records Directory",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
            }

            item {
                VitalSenseTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search Patient",
                    placeholder = "Search by name or village..."
                )
            }

            val filteredPatients = patients.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.villageName.contains(searchQuery, ignoreCase = true)
            }

            items(filteredPatients) { pat ->
                VitalSenseCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = pat.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${pat.villageName} · Age: ${pat.age} (${pat.gender}) · ASHA: ${pat.ashaWorkerName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                        Button(
                            onClick = { selectedPatientForHistory = pat },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = LavenderSecondary, contentColor = TextPrimaryNearBlack),
                            contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                            modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("📋 History", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

    if (showScheduleDialog) {
        ScheduleAppointmentDialog(
            patients = patients,
            onDismiss = { showScheduleDialog = false },
            onPropose = { patientId, patientName, date, timeSlot ->
                onProposeAppointment(patientId, patientName, date, timeSlot)
            }
        )
    }

    selectedPatientForHistory?.let { patient ->
        PatientHistoryDialog(
            patient = patient,
            conditions = allConditions,
            prescriptions = allPrescriptions,
            appointments = appointments,
            onDismiss = { selectedPatientForHistory = null }
        )
    }
}
