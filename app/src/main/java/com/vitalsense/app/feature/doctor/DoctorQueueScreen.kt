package com.vitalsense.app.feature.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.ui.util.AdaptiveScreenContainer
import com.vitalsense.app.core.ui.util.touchSpring
import com.vitalsense.app.feature.doctor.components.DoctorSlotConfigDialog
import com.vitalsense.app.feature.doctor.components.QueueEntryListItem
import com.vitalsense.app.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorQueueScreen(
    doctor: Doctor,
    todaysQueue: List<QueueEntry>,
    slotConfig: DoctorDaySlotConfig?,
    patients: List<Patient>,
    onBackClick: () -> Unit,
    onCallNext: () -> Unit,
    onStartConsultation: (entryId: String) -> Unit,
    onCompleteConsultation: (entryId: String, outcomeNotes: String?) -> Unit,
    onMarkNoShow: (entryId: String) -> Unit,
    onSkip: (entryId: String) -> Unit,
    onPrioritize: (entryId: String) -> Unit,
    onAddWalkIn: (patientId: String, patientName: String) -> Unit,
    onUpdateSlotConfig: (capacity: Int, isWalkInOpen: Boolean, startTime: String, endTime: String) -> Unit
) {
    var showSlotConfigDialog by remember { mutableStateOf(false) }
    var showWalkInDialog by remember { mutableStateOf(false) }
    var outcomeNotesText by remember { mutableStateOf("") }

    val activeConsultation = remember(todaysQueue) {
        todaysQueue.firstOrNull { it.status == QueueEntryStatus.IN_CONSULTATION }
    }
    val calledEntry = remember(todaysQueue) {
        todaysQueue.firstOrNull { it.status == QueueEntryStatus.CALLED }
    }
    val waitingEntries = remember(todaysQueue) {
        todaysQueue.filter { it.status == QueueEntryStatus.WAITING }
    }
    val triagePriorityEntries = remember(waitingEntries, patients) {
        waitingEntries.filter { entry ->
            val p = patients.find { it.id == entry.patientId || it.name.equals(entry.patientName, ignoreCase = true) }
            val isSevereOrHigh = p?.currentRiskLevel == SeverityLevel.SEVERE || p?.currentRiskLevel == SeverityLevel.HIGH
            entry.priorityFlag || isSevereOrHigh
        }.sortedWith(
            compareByDescending<QueueEntry> { it.priorityFlag }
                .thenByDescending { entry ->
                    val p = patients.find { it.id == entry.patientId || it.name.equals(entry.patientName, ignoreCase = true) }
                    when (p?.currentRiskLevel) {
                        SeverityLevel.SEVERE -> 3
                        SeverityLevel.HIGH -> 2
                        SeverityLevel.MODERATE -> 1
                        else -> 0
                    }
                }
                .thenBy { it.tokenNumber }
        )
    }
    val routineEntries = remember(waitingEntries, triagePriorityEntries) {
        waitingEntries.filter { entry -> triagePriorityEntries.none { it.id == entry.id } }
            .sortedBy { it.tokenNumber }
    }
    val completedCount = remember(todaysQueue) {
        todaysQueue.count { it.status == QueueEntryStatus.COMPLETED }
    }

    val currentServingToken = activeConsultation?.tokenNumber
        ?: calledEntry?.tokenNumber
        ?: 0

    AdaptiveScreenContainer { windowCategory ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(R.string.liveQueueTitle),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VS_OnBackground
                            )
                            Text(
                                text = "Dr. ${doctor.name} · ${doctor.specialty}",
                                style = MaterialTheme.typography.labelSmall,
                                color = VS_OnSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = VS_OnBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSlotConfigDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "Configure Slots",
                                tint = VS_Primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = VS_Background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Hero Queue HUD Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = VS_Surface),
                        border = BorderStroke(1.dp, VS_Outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                if (slotConfig?.isWalkInOpen != false) VS_Success else VS_Error,
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = if (slotConfig?.isWalkInOpen != false) "Queue Open" else "Walk-Ins Closed",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (slotConfig?.isWalkInOpen != false) VS_Success else VS_Error
                                    )
                                }

                                Text(
                                    text = "Completed: $completedCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VS_OnSurfaceVariant
                                )
                            }

                            // Big Now Serving Token Badge
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.nowServingTokenCaps),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = VS_OnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentServingToken > 0) "#$currentServingToken" else "--",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 54.sp
                                    ),
                                    color = if (currentServingToken > 0) VS_Primary else VS_OnSurfaceVariant
                                )
                                Text(
                                    text = when {
                                        activeConsultation != null -> "In Consultation: ${activeConsultation.patientName}"
                                        calledEntry != null -> "Called: ${calledEntry.patientName} (Awaiting Enter)"
                                        else -> "No patient currently called"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = VS_OnBackground,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Primary Call Next Action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        when {
                                            activeConsultation != null -> {
                                                onCompleteConsultation(activeConsultation.id, "Consultation completed")
                                                onCallNext()
                                            }
                                            calledEntry != null -> {
                                                onStartConsultation(calledEntry.id)
                                            }
                                            else -> {
                                                onCallNext()
                                            }
                                        }
                                    },
                                    enabled = when {
                                        activeConsultation != null -> true
                                        calledEntry != null -> true
                                        else -> waitingEntries.isNotEmpty()
                                    },
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(52.dp)
                                        .touchSpring(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            activeConsultation != null -> VS_Success
                                            calledEntry != null -> Color(0xFF7C5CFF)
                                            else -> VS_Primary
                                        },
                                        disabledContainerColor = VS_Primary.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Icon(
                                        imageVector = when {
                                            activeConsultation != null -> Icons.Outlined.CheckCircle
                                            calledEntry != null -> Icons.Outlined.PlayArrow
                                            else -> Icons.Outlined.Campaign
                                        },
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            activeConsultation != null -> "Complete & Call Next"
                                            calledEntry != null -> "Start Consultation (#${calledEntry.tokenNumber})"
                                            else -> "Call Next (${waitingEntries.size})"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showWalkInDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .touchSpring(),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.5.dp, VS_Primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PersonAdd,
                                        contentDescription = null,
                                        tint = VS_Primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.walkInLabel),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = VS_Primary
                                    )
                                }
                            }
                        }
                    }
                }

                // 1.5 Triage Prioritization Indicator (Audio 5)
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VS_PrimaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚖️", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "Automated Clinical Triage Prioritization Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = VS_Primary
                                    )
                                )
                                Text(
                                    text = "OPD Queue is prioritized by triage severity (Emergency & High-Risk Maternal first), not FIFO",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = VS_OnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. In Consultation HUD (if active)
                if (activeConsultation != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = BorderStroke(1.5.dp, VS_Success.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("🩺", fontSize = 20.sp)
                                        Column {
                                            Text(
                                                text = stringResource(R.string.activeConsultationLabel),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = VS_Success
                                            )
                                            Text(
                                                text = "${activeConsultation.patientName} (Token #${activeConsultation.tokenNumber})",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = VS_OnBackground
                                            )
                                        }
                                    }

                                    TabularStatusChip(
                                        statusText = "IN ROOM",
                                        containerColor = NagarSevaStatusNormalBg,
                                        textColor = VS_Success
                                    )
                                }

                                VitalSenseTextField(
                                    value = outcomeNotesText,
                                    onValueChange = { outcomeNotesText = it },
                                    label = "Clinical Notes / Diagnosis / Plan",
                                    placeholder = "e.g. Prescribed anti-pyretic, ordered CBC test...",
                                    maxLines = 3
                                )

                                Button(
                                    onClick = {
                                        onCompleteConsultation(activeConsultation.id, outcomeNotesText.ifBlank { null })
                                        outcomeNotesText = ""
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .touchSpring(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VS_Success)
                                ) {
                                    Icon(imageVector = Icons.Outlined.Check, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        stringResource(R.string.completeConsultation),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Waiting Queue Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Waiting in Queue (${waitingEntries.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VS_OnBackground
                            )
                            if (triagePriorityEntries.isNotEmpty()) {
                                Text(
                                    text = "🚨 Triage Priority Sorted First",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                        Text(
                            text = if (triagePriorityEntries.isNotEmpty()) "${triagePriorityEntries.size} Urgent · ${routineEntries.size} Routine" else stringResource(R.string.orderedByCheckIn),
                            style = MaterialTheme.typography.labelSmall,
                            color = VS_OnSurfaceVariant
                        )
                    }
                }

                if (waitingEntries.isEmpty() && calledEntry == null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = VS_Surface),
                            border = BorderStroke(1.dp, VS_Outline)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🎉", fontSize = 32.sp)
                                    Text(
                                        text = stringResource(R.string.queueAllCaughtUp),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = VS_OnBackground
                                    )
                                    Text(
                                        text = stringResource(R.string.noPatientsWaitingNow),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VS_OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Render called entry first if awaiting enter
                    if (calledEntry != null) {
                        item(key = "called_${calledEntry.id}") {
                            val calledPatient = patients.find { it.id == calledEntry.patientId || it.name.equals(calledEntry.patientName, ignoreCase = true) }
                            QueueEntryListItem(
                                entry = calledEntry,
                                patient = calledPatient,
                                isDoctorMode = true,
                                onStartConsultation = { onStartConsultation(calledEntry.id) },
                                onSkip = { onSkip(calledEntry.id) },
                                onMarkNoShow = { onMarkNoShow(calledEntry.id) }
                            )
                        }
                    }

                    // Elevated Clinical Triage Priority Section at TOP OF QUEUE
                    if (triagePriorityEntries.isNotEmpty()) {
                        item(key = "triage_priority_banner") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                border = BorderStroke(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = "🚨", fontSize = 18.sp)
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "CLINICAL TRIAGE PRIORITY",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Black,
                                                        letterSpacing = 0.5.sp
                                                    ),
                                                    color = Color(0xFF991B1B)
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFDC2626)
                                                ) {
                                                    Text(
                                                        text = "TOP OF QUEUE",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.ExtraBold
                                                        ),
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Severe & High Risk Escalations · Ahead of routine queue",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFB91C1C)
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFFEF4444)
                                    ) {
                                        Text(
                                            text = "${triagePriorityEntries.size} Urgent",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        items(triagePriorityEntries, key = { "triage_${it.id}" }) { entry ->
                            val p = patients.find { it.id == entry.patientId || it.name.equals(entry.patientName, ignoreCase = true) }
                            QueueEntryListItem(
                                entry = entry,
                                patient = p,
                                isDoctorMode = true,
                                onPrioritize = { onPrioritize(entry.id) },
                                onSkip = { onSkip(entry.id) },
                                onMarkNoShow = { onMarkNoShow(entry.id) }
                            )
                        }
                    }

                    // Standard Queue Section
                    if (routineEntries.isNotEmpty()) {
                        if (triagePriorityEntries.isNotEmpty()) {
                            item(key = "routine_queue_divider") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Standard Waiting Queue (${routineEntries.size})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VS_OnBackground
                                    )
                                    Text(
                                        text = stringResource(R.string.orderedByCheckIn),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VS_OnSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(routineEntries, key = { "routine_${it.id}" }) { entry ->
                            val p = patients.find { it.id == entry.patientId || it.name.equals(entry.patientName, ignoreCase = true) }
                            QueueEntryListItem(
                                entry = entry,
                                patient = p,
                                isDoctorMode = true,
                                onPrioritize = { onPrioritize(entry.id) },
                                onSkip = { onSkip(entry.id) },
                                onMarkNoShow = { onMarkNoShow(entry.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showSlotConfigDialog) {
        DoctorSlotConfigDialog(
            currentConfig = slotConfig,
            onDismiss = { showSlotConfigDialog = false },
            onSave = onUpdateSlotConfig
        )
    }

    if (showWalkInDialog) {
        WalkInPatientPickerDialog(
            patients = patients,
            onDismiss = { showWalkInDialog = false },
            onSelectPatient = { patient ->
                onAddWalkIn(patient.id, patient.name)
                showWalkInDialog = false
            }
        )
    }
}

@Composable
private fun WalkInPatientPickerDialog(
    patients: List<Patient>,
    onDismiss: () -> Unit,
    onSelectPatient: (Patient) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, patients) {
        if (searchQuery.isBlank()) patients
        else patients.filter { it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.selectWalkInPatient),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = VS_OnBackground
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VitalSenseTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search Patient",
                    placeholder = "Enter name or patient ID..."
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { patient ->
                        Surface(
                            onClick = { onSelectPatient(patient) },
                            shape = RoundedCornerShape(10.dp),
                            color = VS_SurfaceVariant,
                            border = BorderStroke(1.dp, VS_Outline),
                            modifier = Modifier.fillMaxWidth().touchSpring()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = patient.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = VS_OnBackground
                                    )
                                    Text(
                                        text = "Age: ${patient.age} · ${patient.villageName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VS_OnSurfaceVariant
                                    )
                                }
                                Text(stringResource(R.string.selectArrowBtn), fontSize = 12.sp, color = VS_Primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = VS_OnSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = VS_Surface
    )
}
