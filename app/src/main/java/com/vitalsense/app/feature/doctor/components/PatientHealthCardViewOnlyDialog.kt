package com.vitalsense.app.feature.doctor.components

import androidx.compose.foundation.BorderStroke
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
import com.vitalsense.app.core.ui.components.VitalSenseButton
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
            shape = DialogShape,
            color = GlumeSurfaceCard,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, GlumeBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Header with Read-Only Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🪪 Patient Health Card",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Surface(
                            shape = PillShape,
                            color = GlumePrimaryPurpleContainer,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "🔒 VIEW-ONLY ACCESS (§3 PRD Rule)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp),
                                color = GlumePrimaryPurpleLight
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text(text = "✕", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextSecondary)
                    }
                }

                HorizontalDivider(color = GlumeBorder)

                // Demographic Card
                VitalSenseCard(
                    backgroundColor = GlumeSurfaceElevated,
                    border = BorderStroke(1.dp, GlumeBorder)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = patient.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = "Age: ${patient.age} yrs · Gender: ${patient.gender} · Village: ${patient.villageName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }
                            SeverityBadge(severity = patient.currentRiskLevel)
                        }

                        HorizontalDivider(color = GlumeBorder, modifier = Modifier.padding(vertical = Spacing.xxs))

                        Text(
                            text = "📞 Contact: ${patient.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "🚨 Emergency Contact: ${patient.emergencyContact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "🤝 Assigned ASHA: ${patient.ashaWorkerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                }

                // Clinical History Card
                VitalSenseCard(
                    backgroundColor = GlumeSurfaceElevated,
                    border = BorderStroke(1.dp, GlumeBorder)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = "📋 Latest Reported Condition",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = patient.lastCondition,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlumeTextPrimary
                        )
                        Row(
                            modifier = Modifier.padding(top = Spacing.xxs),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            Text(
                                text = "Last Checkup: ${patient.lastVisitDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                            Text(
                                text = "Next Appt: ${patient.nextAppointmentDate ?: "None"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                        }
                    }
                }

                VitalSenseButton(
                    text = "Close Health Card",
                    onClick = onDismiss,
                    style = com.vitalsense.app.core.ui.components.ButtonStyle.PRIMARY
                )
            }
        }
    }
}
