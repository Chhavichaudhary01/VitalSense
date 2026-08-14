package com.vitalsense.app.feature.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*

@Composable
fun DoctorHomeScreen(
    doctor: Doctor,
    pendingConditions: List<ConditionRecord>,
    appointments: List<Appointment>,
    dispensaryStock: List<DispensaryItem>,
    onRespondClick: (ConditionRecord) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // 1. Doctor Header
        item {
            Column {
                Text(
                    text = doctor.name,
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = "${doctor.specialty.displayName} · ${doctor.hospitalName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }

        // 2. Metrics summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = LimePrimary.copy(alpha = 0.5f)
                ) {
                    Column {
                        Text(
                            text = "${pendingConditions.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Pending Cases",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                VitalSenseCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = LavenderSecondary.copy(alpha = 0.4f)
                ) {
                    Column {
                        Text(
                            text = "${appointments.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Appointments",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // 3. Section: Pending Patient Cases Queue
        item {
            Text(
                text = "Pending Clinical Cases (${pendingConditions.size})",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimaryNearBlack
            )
        }

        items(pendingConditions) { record ->
            VitalSenseCard(elevation = 2.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.patientName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Village: ${record.villageName} · Category: ${record.category.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                        SeverityBadge(severity = record.severity)
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
                        if (record.ashaProxyLogged) {
                            Surface(shape = PillShape, color = LavenderSecondary.copy(alpha = 0.4f)) {
                                Text(
                                    text = "Logged by ASHA Helper",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = { onRespondClick(record) },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal)
                        ) {
                            Text(text = "Prescribe & Guide →", color = LimePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 4. Section: Upcoming Appointments
        item {
            Text(
                text = "Scheduled Appointments",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimaryNearBlack
            )
        }

        items(appointments) { appointment ->
            VitalSenseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = appointment.patientName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${appointment.dateFormatted} at ${appointment.timeSlot}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Surface(
                        shape = PillShape,
                        color = if (appointment.status == "Confirmed") SoftMintSuccess.copy(alpha = 0.5f) else AmberWarning.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = appointment.status,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 5. Section: Mock Dispensary Stock Check (PRD §3.3 & §4.5)
        item {
            Text(
                text = "Dispensary Stock & Medicine Availability",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimaryNearBlack
            )
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${item.availableQuantity} ${item.unit}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isLowStock) CoralAlert else TextPrimaryNearBlack
                            )
                        )
                        if (item.isLowStock) {
                            Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.2f)) {
                                Text(
                                    text = "LOW",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CoralAlert, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
