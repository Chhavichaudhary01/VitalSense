package com.vitalsense.app.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.DoctorQueueSummary
import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.ui.components.TabularStatusChip
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.ui.util.AdaptiveScreenContainer
import com.vitalsense.app.core.ui.util.touchSpring
import com.vitalsense.app.feature.doctor.components.QueueEntryListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueOversightScreen(
    summaries: List<DoctorQueueSummary>,
    selectedDoctorId: String?,
    selectedDoctorQueue: List<QueueEntry>,
    onSelectDoctor: (doctorId: String) -> Unit,
    onClearSelectedDoctor: () -> Unit,
    onBackClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val selectedDoctorSummary = remember(summaries, selectedDoctorId) {
        summaries.firstOrNull { it.doctorId == selectedDoctorId }
    }

    AdaptiveScreenContainer {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (selectedDoctorSummary != null) "Dr. ${selectedDoctorSummary.doctorName} Queue" else strings.liveQueueTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = if (selectedDoctorSummary != null) "Real-time doctor queue drilldown (Read-Only)" else "Multi-Clinic Patient Wait Time Monitor",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (selectedDoctorId != null) onClearSelectedDoctor()
                                else onBackClick()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GlumeTextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = NagarSevaCanvasLight
        ) { paddingValues ->
            if (selectedDoctorId != null) {
                // Doctor Queue Drilldown View (Read-Only)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NagarSevaSurfaceLight),
                            border = BorderStroke(1.dp, NagarSevaBorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Current Serving Token",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GlumeTextSecondary
                                    )
                                    Text(
                                        text = if ((selectedDoctorSummary?.currentToken ?: 0) > 0) "#${selectedDoctorSummary?.currentToken}" else "None",
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                        color = NagarSevaPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Waiting in Line",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GlumeTextSecondary
                                    )
                                    Text(
                                        text = "${selectedDoctorSummary?.waitingCount ?: 0} Patients",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Live Patients in Queue (${selectedDoctorQueue.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                    }

                    if (selectedDoctorQueue.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = NagarSevaSurfaceLight)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No patients in queue for this doctor today.", color = GlumeTextSecondary)
                                }
                            }
                        }
                    } else {
                        items(selectedDoctorQueue, key = { it.id }) { entry ->
                            QueueEntryListItem(
                                entry = entry,
                                isDoctorMode = false // Read-only oversight
                            )
                        }
                    }
                }
            } else {
                // Summary Matrix Across All Doctors
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Doctors & Clinic Streams (${summaries.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "Tap doctor to inspect queue",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                        }
                    }

                    items(summaries, key = { it.doctorId }) { summary ->
                        Card(
                            onClick = { onSelectDoctor(summary.doctorId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .touchSpring(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NagarSevaSurfaceLight),
                            border = BorderStroke(1.dp, NagarSevaBorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = NagarSevaPrimary.copy(alpha = 0.12f),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("👨‍⚕️", fontSize = 18.sp)
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = "Dr. ${summary.doctorName}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = GlumeTextPrimary
                                            )
                                            Text(
                                                text = if (summary.isQueueOpen) "Clinic Open · Accepting Patients" else "Clinic Closed",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (summary.isQueueOpen) NagarSevaStatusNormal else GlumeTextSecondary
                                            )
                                        }
                                    }

                                    TabularStatusChip(
                                        statusText = if (summary.waitingCount > 5) "HIGH LOAD" else "NORMAL",
                                        containerColor = if (summary.waitingCount > 5) NagarSevaStatusUrgentBg else NagarSevaStatusNormalBg,
                                        textColor = if (summary.waitingCount > 5) NagarSevaStatusUrgent else NagarSevaStatusNormal
                                    )
                                }

                                HorizontalDivider(color = NagarSevaBorderLight)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Now Serving", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                                        Text(
                                            text = if (summary.currentToken > 0) "#${summary.currentToken}" else "--",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = NagarSevaPrimary
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("In Waiting", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                                        Text(
                                            text = "${summary.waitingCount}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeTextPrimary
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Avg Wait", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                                        Text(
                                            text = "~${(summary.avgWaitSeconds + 59) / 60}m",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
