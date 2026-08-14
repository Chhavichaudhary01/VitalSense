package com.vitalsense.app.feature.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*

@Composable
fun PatientHomeScreen(
    patient: Patient,
    onCategoryClick: (ConditionCategory) -> Unit = {},
    onViewHealthCard: () -> Unit = {},
    onTriggerSos: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSosConfirmation by remember { mutableStateOf(false) }
    var sosSentSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // 1. Personalized Greeting
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Namaste, ${patient.name.split(" ").first()}",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Village: ${patient.villageName} · ASHA: ${patient.ashaWorkerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    SeverityBadge(severity = patient.currentRiskLevel)
                }
            }
        }

        // 2. Inline Dismissible Page Guide (PRD §4.9)
        item {
            InlineHelpBanner(
                title = "Your Rural Health Portal",
                message = "Tap any health category below to log your symptoms, check prescriptions, or connect with your ASHA helper."
            )
        }

        // 3. Hero Card: Health Card & Daily Status (Design Docs §4.1)
        item {
            VitalSenseCard(
                backgroundColor = LimePrimary.copy(alpha = 0.85f),
                elevation = 3.dp,
                onClick = onViewHealthCard
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🪪", fontSize = 18.sp)
                            Text(
                                text = "OFFLINE HEALTH CARD",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = DarkCharcoal
                            )
                        }
                        Surface(
                            shape = PillShape,
                            color = DarkCharcoal
                        ) {
                            Text(
                                text = "View Full Card →",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LimePrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Active Condition: ${patient.lastCondition}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimaryNearBlack
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Checkup: ${patient.nextAppointmentDate ?: "None Scheduled"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryNearBlack.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Cached Offline ✓",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )
                    }
                }
            }
        }

        // 4. Section Title: "How can I help you today?"
        item {
            Text(
                text = "How can I help you today?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimaryNearBlack
            )
        }

        // 5. 2-Column Category Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val categories = ConditionCategory.values().toList()
                val chunked = categories.chunked(2)

                chunked.forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowCategories.forEach { category ->
                            VitalSenseCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(105.dp),
                                backgroundColor = Color(category.colorHex).copy(alpha = 0.35f),
                                onClick = { onCategoryClick(category) }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceWhite),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = category.emoji, fontSize = 18.sp)
                                    }

                                    Column {
                                        Text(
                                            text = category.displayName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            ),
                                            color = TextPrimaryNearBlack
                                        )
                                        Text(
                                            text = "Tap to open",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }
                            }
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 6. Persistent Emergency SOS Banner (Coral / High Visibility)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            VitalSenseCard(
                backgroundColor = CoralAlert,
                elevation = 4.dp,
                onClick = { showSosConfirmation = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚨", fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                text = "EMERGENCY SOS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                            )
                            Text(
                                text = "Alert ASHA & Family (Works via SMS)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SurfaceWhite.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                    Text(
                        text = "TRIGGER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    )
                }
            }
        }
    }

    // SOS Confirmation Bottom Sheet / Dialog
    if (showSosConfirmation) {
        AlertDialog(
            onDismissRequest = { showSosConfirmation = false },
            title = {
                Text(
                    text = "🚨 Confirm Emergency SOS",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "This will immediately send an Emergency SOS alert with your location to:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• ASHA Helper: ${patient.ashaWorkerName}\n• Emergency Contact: ${patient.emergencyContact}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "📡 Falls back to cellular SMS if mobile internet is unavailable.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSosConfirmation = false
                        sosSentSuccess = true
                        onTriggerSos()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                ) {
                    Text("Send SOS Now", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosConfirmation = false }) {
                    Text("Cancel", color = TextPrimaryNearBlack)
                }
            }
        )
    }

    if (sosSentSuccess) {
        AlertDialog(
            onDismissRequest = { sosSentSuccess = false },
            title = {
                Text("✅ SOS Alert Broadcasted", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Emergency alert with your GPS coordinates has been transmitted to ${patient.ashaWorkerName}. Keep your phone nearby.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { sosSentSuccess = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal)
                ) {
                    Text("Understood", color = LimePrimary)
                }
            }
        )
    }
}
