package com.vitalsense.app.feature.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.SeverityBadge
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@Composable
fun HealthCardViewerScreen(patient: Patient) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GlumeBackground)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            text = "Offline Health Card",
            style = MaterialTheme.typography.displayMedium,
            color = GlumeTextPrimary
        )

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
                            text = patient.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Age: ${patient.age} · Gender: ${patient.gender} · Village: ${patient.villageName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                    SeverityBadge(severity = patient.currentRiskLevel)
                }

                HorizontalDivider(color = GlumeBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Blood Group", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                        Text("O+ Positive", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                    }
                    Column {
                        Text("Allergies", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                        Text("None Reported", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                    }
                    Column {
                        Text("Emergency", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                        Text(patient.emergencyContact, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GlumeAlertCoral)
                    }
                }

                Surface(
                    shape = CardShape,
                    color = GlumeSurfaceElevated,
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(text = "🔲", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "Permanent Offline QR Identity",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumePrimaryPurpleLight
                            )
                            Text(
                                text = "UID: ${patient.id} · Cached Offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}