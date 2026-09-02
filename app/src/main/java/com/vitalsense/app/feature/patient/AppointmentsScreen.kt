package com.vitalsense.app.feature.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.Appointment
import com.vitalsense.app.core.ui.components.TabularStatusChip
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.ui.util.AdaptiveScreenContainer
import com.vitalsense.app.core.ui.util.touchSpring
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    appointments: List<Appointment>,
    onRequestNew: () -> Unit,
    onBackClick: () -> Unit,
    onCheckIn: (appointmentId: String) -> Unit,
    onViewLiveQueue: () -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    AdaptiveScreenContainer {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Appointments",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onRequestNew,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .touchSpring(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NagarSevaPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Book Visit", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onViewLiveQueue,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .touchSpring(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, NagarSevaPrimary)
                        ) {
                            Text("Live Queue HUD", fontWeight = FontWeight.Bold, color = NagarSevaPrimary)
                        }
                    }
                }

                if (appointments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
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
                                    Text("📅", fontSize = 32.sp)
                                    Text(
                                        text = "No appointments scheduled",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                    Text(
                                        text = "Book a new clinical consultation with a village doctor.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GlumeTextSecondary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(appointments, key = { it.id }) { appt ->
                        val isToday = appt.dateFormatted == today
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .touchSpring(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NagarSevaSurfaceLight),
                            border = BorderStroke(
                                1.dp,
                                if (isToday) NagarSevaPrimary.copy(alpha = 0.4f) else NagarSevaBorderLight
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Dr. ${appt.doctorName}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeTextPrimary
                                        )
                                        Text(
                                            text = "${appt.dateFormatted} · ${appt.timeSlot}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GlumeTextSecondary
                                        )
                                    }

                                    TabularStatusChip(
                                        statusText = appt.status.uppercase(),
                                        containerColor = if (appt.status.contains("Confirm", ignoreCase = true)) NagarSevaStatusNormalBg else NagarSevaStatusProgressBg,
                                        textColor = if (appt.status.contains("Confirm", ignoreCase = true)) NagarSevaStatusNormal else NagarSevaStatusProgress
                                    )
                                }

                                if (isToday) {
                                    Button(
                                        onClick = { onCheckIn(appt.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .touchSpring(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NagarSevaPrimary)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Check In for Today's Visit",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
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