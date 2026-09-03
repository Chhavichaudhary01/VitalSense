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
import androidx.compose.material.icons.filled.*
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
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "Dr. ${doctor.name} · ${doctor.specialty}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GlumeTextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSlotConfigDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Configure Slots",
                                tint = NagarSevaPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = NagarSevaCanvasLight
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
                        colors = CardDefaults.cardColors(containerColor = NagarSevaSurfaceLight),
                        border = BorderStroke(1.dp, NagarSevaBorderLight),
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
                                                if (slotConfig?.isWalkInOpen != false) NagarSevaStatusNormal else NagarSevaStatusUrgent,
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = if (slotConfig?.isWalkInOpen != false) "Queue Open" else "Walk-Ins Closed",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (slotConfig?.isWalkInOpen != false) NagarSevaStatusNormal else NagarSevaStatusUrgent
                                    )
                                }

                                Text(
                                    text = "Completed: $completedCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GlumeTextSecondary
                                )
                            }

                            // Big Now Serving Token Badge
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "NOW SERVING TOKEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = GlumeTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentServingToken > 0) "#$currentServingToken" else "--",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 54.sp
                                    ),
                                    color = if (currentServingToken > 0) NagarSevaPrimary else GlumeTextSecondary
                                )
                                Text(
                                    text = when {
                                        activeConsultation != null -> "In Consultation: ${activeConsultation.patientName}"
                                        calledEntry != null -> "Called: ${calledEntry.patientName} (Awaiting Enter)"
                                        else -> "No patient currently called"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = GlumeTextPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Primary Call Next Action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onCallNext,
                                    enabled = waitingEntries.isNotEmpty() && activeConsultation == null,
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(52.dp)
                                        .touchSpring(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NagarSevaPrimary,
                                        disabledContainerColor = NagarSevaPrimary.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Call Next (${waitingEntries.size})",
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
                                    border = BorderStroke(1.5.dp, NagarSevaPrimary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = NagarSevaPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Walk-In",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NagarSevaPrimary
                                    )
                                }
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
                            border = BorderStroke(1.5.dp, NagarSevaStatusNormal.copy(alpha = 0.5f))
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
                                                text = "Active Consultation",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = NagarSevaStatusNormal
                                            )
                                            Text(
                                                text = "${activeConsultation.patientName} (Token #${activeConsultation.tokenNumber})",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = GlumeTextPrimary
                                            )
                                        }
                                    }

                                    TabularStatusChip(
                                        statusText = "IN ROOM",
                                        containerColor = NagarSevaStatusNormalBg,
                                        textColor = NagarSevaStatusNormal
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
                                    colors = ButtonDefaults.buttonColors(containerColor = NagarSevaStatusNormal)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Complete Consultation",
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
                        Text(
                            text = "Waiting in Queue (${waitingEntries.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Ordered by Check-In",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumeTextSecondary
                        )
                    }
                }

                if (waitingEntries.isEmpty() && calledEntry == null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NagarSevaSurfaceLight),
                            border = BorderStroke(1.dp, NagarSevaBorderLight)
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
                                        text = "Queue is all caught up!",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                    Text(
                                        text = "No patients are currently waiting.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GlumeTextSecondary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Render called entry first if awaiting enter
                    if (calledEntry != null) {
                        item(key = "called_${calledEntry.id}") {
                            QueueEntryListItem(
                                entry = calledEntry,
                                isDoctorMode = true,
                                onStartConsultation = { onStartConsultation(calledEntry.id) },
                                onSkip = { onSkip(calledEntry.id) },
                                onMarkNoShow = { onMarkNoShow(calledEntry.id) }
                            )
                        }
                    }

                    items(waitingEntries, key = { it.id }) { entry ->
                        QueueEntryListItem(
                            entry = entry,
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
                text = "Select Walk-In Patient",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = GlumeTextPrimary
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
                            color = NagarSevaElevatedLight,
                            border = BorderStroke(1.dp, NagarSevaBorderLight),
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
                                        color = GlumeTextPrimary
                                    )
                                    Text(
                                        text = "Age: ${patient.age} · ${patient.villageName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GlumeTextSecondary
                                    )
                                }
                                Text("Select →", fontSize = 12.sp, color = NagarSevaPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GlumeTextSecondary)
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = NagarSevaSurfaceLight
    )
}
