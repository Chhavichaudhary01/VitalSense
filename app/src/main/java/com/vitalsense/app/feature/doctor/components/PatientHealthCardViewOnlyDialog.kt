package com.vitalsense.app.feature.doctor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.SeverityBadge
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@Composable
fun PatientHealthCardViewOnlyDialog(
    patient: Patient,
    onDismiss: () -> Unit
) {
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
                // Header with Read-Only Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🪪 Patient Health Card",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryNearBlack
                            )
                        }
                        Surface(
                            shape = PillShape,
                            color = LavenderSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "🔒 VIEW-ONLY ACCESS (§3 PRD Rule)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = TextPrimaryNearBlack
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Text(text = "✕", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Demographic Card
                VitalSenseCard(backgroundColor = SurfaceWhite, elevation = 1.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = patient.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryNearBlack
                                )
                                Text(
                                    text = "Age: ${patient.age} yrs · Gender: ${patient.gender} · Village: ${patient.villageName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                            SeverityBadge(severity = patient.currentRiskLevel)
                        }

                        HorizontalDivider(color = Color(0xFFF0EDE6), modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "📞 Contact: ${patient.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "🚨 Emergency Contact: ${patient.emergencyContact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "🤝 Assigned ASHA: ${patient.ashaWorkerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                // Clinical History Card
                VitalSenseCard(backgroundColor = SurfaceWhite, elevation = 1.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "📋 Latest Reported Condition",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = patient.lastCondition,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Last Checkup: ${patient.lastVisitDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = "Next Appt: ${patient.nextAppointmentDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal)
                ) {
                    Text(text = "Close Health Card", color = LimePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
