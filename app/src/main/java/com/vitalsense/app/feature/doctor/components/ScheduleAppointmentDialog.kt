package com.vitalsense.app.feature.doctor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointmentDialog(
    patients: List<Patient>,
    onDismiss: () -> Unit,
    onPropose: (patientId: String, patientName: String, date: String, timeSlot: String) -> Unit
) {
    var selectedPatient by remember { mutableStateOf(patients.firstOrNull()) }
    var selectedDate by remember { mutableStateOf("19 Aug 2026") }
    var selectedTimeSlot by remember { mutableStateOf("10:30 AM") }
    var patientExpanded by remember { mutableStateOf(false) }

    val sampleDates = listOf("18 Aug 2026", "19 Aug 2026", "20 Aug 2026", "21 Aug 2026")
    val sampleSlots = listOf("09:30 AM", "10:30 AM", "11:30 AM", "02:30 PM", "04:00 PM")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CardShape,
            color = WarmCreamBackground,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📅 Schedule New Appointment",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Propose consultation time to patient",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text(text = "✕", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Select Patient Dropdown
                Text(
                    text = "Select Patient:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )

                ExposedDropdownMenuBox(
                    expanded = patientExpanded,
                    onExpandedChange = { patientExpanded = !patientExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPatient?.let { "${it.name} (${it.villageName})" } ?: "Select Patient",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = patientExpanded,
                        onDismissRequest = { patientExpanded = false }
                    ) {
                        patients.forEach { pat ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = pat.name, fontWeight = FontWeight.Bold)
                                        Text(text = "${pat.villageName} · ${pat.phone}", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted)
                                    }
                                },
                                onClick = {
                                    selectedPatient = pat
                                    patientExpanded = false
                                }
                            )
                        }
                    }
                }

                // Select Date
                Text(
                    text = "Select Date:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sampleDates.take(3).forEach { date ->
                        val isSelected = selectedDate == date
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDate = date },
                            label = { Text(date, fontSize = 11.sp) },
                            shape = PillShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DarkCharcoal,
                                selectedLabelColor = LimePrimary
                            )
                        )
                    }
                }

                // Select Time Slot
                Text(
                    text = "Select Time Slot:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sampleSlots.take(3).forEach { slot ->
                        val isSelected = selectedTimeSlot == slot
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTimeSlot = slot },
                            label = { Text(slot, fontSize = 11.sp) },
                            shape = PillShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DarkCharcoal,
                                selectedLabelColor = LimePrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = PillShape
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            selectedPatient?.let { pat ->
                                onPropose(pat.id, pat.name, selectedDate, selectedTimeSlot)
                                onDismiss()
                            }
                        },
                        enabled = selectedPatient != null,
                        modifier = Modifier.weight(1.4f),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderSecondary, contentColor = TextPrimaryNearBlack)
                    ) {
                        Text("Propose Appt ✓", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
