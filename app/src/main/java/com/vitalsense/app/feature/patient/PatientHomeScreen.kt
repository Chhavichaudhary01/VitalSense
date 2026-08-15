package com.vitalsense.app.feature.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    notices: List<BroadcastNotice> = emptyList(),
    onCategoryClick: (ConditionCategory) -> Unit = {},
    onViewHealthCard: () -> Unit = {},
    onTriggerSos: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSosConfirmation by remember { mutableStateOf(false) }
    var sosSentSuccess by remember { mutableStateOf(false) }

    val adminAdvisories = notices.filter {
        it.senderRole == UserRole.ADMIN || it.targetRole == "ALL" || it.targetRole == "PATIENT"
    }

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
        
        item {
            Text("Other Services", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp), color = TextPrimaryNearBlack)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VitalSenseButton("My Prescriptions", onClick = { }, modifier = Modifier.fillMaxWidth())
                VitalSenseButton("My Appointments", onClick = { }, modifier = Modifier.fillMaxWidth())
                VitalSenseButton("Find Doctors (Map)", onClick = { }, modifier = Modifier.fillMaxWidth())
                VitalSenseButton("Government Schemes", onClick = { }, modifier = Modifier.fillMaxWidth())
                VitalSenseButton("Upload Prescription (OCR)", onClick = { }, modifier = Modifier.fillMaxWidth())
                VitalSenseButton("Help / Manual", onClick = { }, modifier = Modifier.fillMaxWidth())
            }
        }

        // 5. District Health Advisories (Admin Broadcasts)
        if (adminAdvisories.isNotEmpty()) {
            item {
                Text(
                    text = "📢 District Health Advisories",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPrimaryNearBlack
                )
            }

            items(adminAdvisories) { advisory ->
                VitalSenseCard(
                    backgroundColor = if (advisory.isUrgent) CoralAlert.copy(alpha = 0.15f) else SurfaceWhite
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = advisory.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (advisory.isUrgent) CoralAlert else TextPrimaryNearBlack
                            )
                            if (advisory.isUrgent) {
                                Surface(shape = PillShape, color = CoralAlert.copy(alpha = 0.2f)) {
                                    Text(
                                        text = "URGENT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CoralAlert),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = advisory.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Issued by: ${advisory.senderName} (${advisory.senderRole.name})",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
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

    val context = androidx.compose.ui.platform.LocalContext.current

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
                        text = "📡 Falls back to cellular SMS with GPS location if mobile internet is unavailable.",
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
                    Text("Broadcast SOS Alert", color = SurfaceWhite, fontWeight = FontWeight.Bold)
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
        val sosMsg = com.vitalsense.app.core.util.EmergencySosHelper.createSosMessage(patient)
        AlertDialog(
            onDismissRequest = { sosSentSuccess = false },
            title = {
                Text("🚨 SOS Alert Active", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Emergency broadcast sent with your GPS location to ${patient.ashaWorkerName} and health monitoring.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Divider()

                    Text(
                        text = "Zero Internet Fallbacks:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryNearBlack
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                com.vitalsense.app.core.util.EmergencySosHelper.sendCellularSmsFallback(
                                    context = context,
                                    recipientPhone = patient.emergencyContact,
                                    message = sosMsg
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = PillShape
                        ) {
                            Text("💬 SMS ASHA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                com.vitalsense.app.core.util.EmergencySosHelper.dialEmergencyCall(
                                    context = context,
                                    phoneNumber = "108"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                        ) {
                            Text("📞 Call 108", color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { sosSentSuccess = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal)
                ) {
                    Text("Done", color = LimePrimary)
                }
            }
        )
    }
}
