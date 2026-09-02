package com.vitalsense.app.feature.ot

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
import com.vitalsense.app.core.data.model.OtSurgeryBooking
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseTextField
import com.vitalsense.app.core.ui.theme.*

@Composable
fun OtSchedulerScreen(
    bookings: List<OtSurgeryBooking>,
    onBackClick: () -> Unit,
    onBookSurgery: (OtSurgeryBooking) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBookSurgeryDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlumeBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.md, bottom = Spacing.xxl)
    ) {
        // 1. Header with Back Navigation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = PillShape,
                    color = GlumeSurfaceCard,
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text("←", color = GlumeTextPrimary, fontWeight = FontWeight.Bold)
                        Text("Doctor Desk", style = MaterialTheme.typography.labelMedium, color = GlumeTextPrimary)
                    }
                }

                Surface(
                    shape = PillShape,
                    color = GlumePrimaryPurpleContainer,
                    border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Surgical Care · OT Module",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumePrimaryPurpleLight,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                    )
                }
            }
        }

        // 2. Hero Operation Theatre HUD
        item {
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                border = BorderStroke(1.dp, GlumeBorder)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🔪 Operation Theatre & Surgical Desk",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "Major OT-1, Trauma OT-2 & Emergency Minor OT Suites",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
                        }

                        Button(
                            onClick = { showBookSurgeryDialog = true },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GlumePrimaryPurple,
                                contentColor = GlumeTextPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                            modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("+ Book OT Slot", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    HorizontalDivider(color = GlumeBorderSubtle)

                    // Chief Surgeon Spotlight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GlumePrimaryPurpleContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👨‍⚕️", fontSize = 20.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lead Surgeon: Dr. Ayushman Dev Singh",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "MDS, Maxillofacial Trauma & Reconstructive Surgery",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumePrimaryPurpleLight
                            )
                        }
                    }
                }
            }
        }

        // 3. Upcoming Surgical Schedule
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Surgical Roster & Bookings (${bookings.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
                Text(
                    text = "PAC Validated",
                    style = MaterialTheme.typography.labelSmall,
                    color = GlumeSuccessMint
                )
            }
        }

        if (bookings.isEmpty()) {
            item {
                VitalSenseCard {
                    Text(
                        text = "No surgical procedures currently scheduled in OT.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )
                }
            }
        }

        items(bookings, key = { it.id }) { booking ->
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                border = BorderStroke(1.dp, if (booking.status == "Completed") GlumeBorder else GlumePrimaryPurple.copy(alpha = 0.4f))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Surface(
                                shape = PillShape,
                                color = GlumeSurfaceElevated,
                                border = BorderStroke(1.dp, GlumeBorder)
                            ) {
                                Text(
                                    text = booking.otRoomName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary,
                                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "· ${booking.scheduledDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = if (booking.pacCleared) GlumeSuccessContainer else GlumeAlertContainer
                        ) {
                            Text(
                                text = if (booking.pacCleared) "✓ PAC CLEARED" else "⚠ PAC PENDING",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = if (booking.pacCleared) GlumeSuccessMint else GlumeAlertCoral,
                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = booking.surgeryName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )

                    HorizontalDivider(color = GlumeBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Patient", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(booking.patientName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                        }
                        Column {
                            Text("Time Slot", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(booking.scheduledTimeSlot, style = MaterialTheme.typography.bodySmall, color = GlumeTextSecondary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Operating Surgeon", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(booking.surgeonName, style = MaterialTheme.typography.labelSmall, color = GlumePrimaryPurpleLight)
                        }
                        Column {
                            Text("Anesthetist", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(booking.anesthetistName, style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                        }
                    }
                }
            }
        }
    }

    // Book OT Slot Dialog
    if (showBookSurgeryDialog) {
        var patientName by remember { mutableStateOf("") }
        var surgeryName by remember { mutableStateOf("") }
        var otRoom by remember { mutableStateOf("Trauma & Ortho OT-2") }
        var surgeonName by remember { mutableStateOf("Dr. Ayushman Dev Singh") }
        var anesthetistName by remember { mutableStateOf("Dr. S. K. Verma (Sr. Anesthetist)") }
        var date by remember { mutableStateOf("Tomorrow") }
        var timeSlot by remember { mutableStateOf("09:00 AM - 11:30 AM") }
        var pacCleared by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showBookSurgeryDialog = false },
            title = {
                Text(
                    text = "Book Operation Theatre Slot",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    VitalSenseTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = "Patient Full Name",
                        placeholder = "e.g. Ramesh Kumar"
                    )

                    VitalSenseTextField(
                        value = surgeryName,
                        onValueChange = { surgeryName = it },
                        label = "Surgical Procedure Name",
                        placeholder = "e.g. Open Reduction & Internal Fixation"
                    )

                    VitalSenseTextField(
                        value = otRoom,
                        onValueChange = { otRoom = it },
                        label = "OT Suite Room",
                        placeholder = "e.g. Major OT-1 or Trauma OT-2"
                    )

                    VitalSenseTextField(
                        value = surgeonName,
                        onValueChange = { surgeonName = it },
                        label = "Primary Operating Surgeon",
                        placeholder = "Dr. Ayushman Dev Singh"
                    )

                    VitalSenseTextField(
                        value = anesthetistName,
                        onValueChange = { anesthetistName = it },
                        label = "Attending Anesthetist",
                        placeholder = "Dr. S. K. Verma"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        VitalSenseTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = "Date",
                            placeholder = "Tomorrow",
                            modifier = Modifier.weight(1f)
                        )
                        VitalSenseTextField(
                            value = timeSlot,
                            onValueChange = { timeSlot = it },
                            label = "Time Slot",
                            placeholder = "09:00 AM",
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pre-Anesthesia Checkup (PAC) Cleared",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumeTextPrimary
                        )
                        Switch(
                            checked = pacCleared,
                            onCheckedChange = { pacCleared = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlumeTextPrimary,
                                checkedTrackColor = GlumePrimaryPurple
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBooking = OtSurgeryBooking(
                            id = "ot_${System.currentTimeMillis()}",
                            otRoomName = otRoom.ifBlank { "Major OT-1" },
                            patientId = "pat_booked",
                            patientName = patientName.ifBlank { "Admitted Patient" },
                            surgeryName = surgeryName.ifBlank { "Surgical Exploration" },
                            surgeonName = surgeonName.ifBlank { "Dr. Ayushman Dev Singh" },
                            anesthetistName = anesthetistName.ifBlank { "Duty Anesthetist" },
                            scheduledDate = date.ifBlank { "Tomorrow" },
                            scheduledTimeSlot = timeSlot.ifBlank { "10:00 AM - 12:00 PM" },
                            pacCleared = pacCleared,
                            status = "Scheduled"
                        )
                        onBookSurgery(newBooking)
                        showBookSurgeryDialog = false
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                    enabled = patientName.isNotBlank() && surgeryName.isNotBlank()
                ) {
                    Text("Confirm OT Slot", style = MaterialTheme.typography.labelSmall)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookSurgeryDialog = false }) {
                    Text("Cancel", color = GlumeTextSecondary)
                }
            },
            containerColor = GlumeSurfaceCard,
            tonalElevation = 6.dp
        )
    }
}
