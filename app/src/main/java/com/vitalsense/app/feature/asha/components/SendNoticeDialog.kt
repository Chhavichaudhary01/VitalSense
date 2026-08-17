package com.vitalsense.app.feature.asha.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*
import java.util.UUID

@Composable
fun SendNoticeDialog(
    asha: AshaWorker,
    onDismiss: () -> Unit,
    onSend: (BroadcastNotice) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedVillage by remember { mutableStateOf(asha.assignedVillages.firstOrNull() ?: "All Assigned Villages") }
    var isUrgent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val quickNoticeTemplates = listOf(
        "💉 Polio / Child Immunization Camp tomorrow at PHC",
        "🦟 Dengue & Malaria Spraying scheduled this week",
        "🤱 Maternal Prenatal Care Checkup drive on Thursday",
        "💊 Free Vitamin-A & IFA Supplement distribution at Anganwadi"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = GlumeSurfaceCard,
            border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.6f)),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Header
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
                            shape = CircleShape,
                            color = GlumePrimaryPurpleContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📢", fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Broadcast Village Advisory",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "By ASHA: ${asha.name}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = GlumeTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", color = GlumeTextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = GlumeBorder)

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(color = GlumeAlertCoral, fontWeight = FontWeight.Bold)
                    )
                }

                // Quick Templates
                Text(
                    text = "QUICK ADVISORY TEMPLATES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = GlumeTextSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    quickNoticeTemplates.forEach { template ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GlumeSurfaceElevated,
                            border = BorderStroke(1.dp, GlumeBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    title = template.substringAfter(" ")
                                    message = "All eligible families in ${selectedVillage} are requested to attend. Contact ASHA ${asha.name} (${asha.phone}) for assistance."
                                    errorMessage = ""
                                }
                        ) {
                            Text(
                                text = template,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GlumeTextPrimary),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Notice Title
                VitalSenseTextField(
                    value = title,
                    onValueChange = { title = it ; errorMessage = "" },
                    label = "Advisory Title / Headline",
                    placeholder = "e.g. Immunization Camp Tomorrow"
                )

                // Notice Message Body
                VitalSenseTextField(
                    value = message,
                    onValueChange = { message = it ; errorMessage = "" },
                    label = "Detailed Message",
                    placeholder = "Enter timings, location, and guidelines for villagers..."
                )

                // Village Selector
                Text(
                    text = "BROADCAST TARGET VILLAGE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = GlumeTextSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (listOf("All Villages") + asha.assignedVillages).forEach { v ->
                        val isSelected = selectedVillage == v
                        Surface(
                            shape = PillShape,
                            color = if (isSelected) GlumePrimaryPurple else GlumeSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) GlumePrimaryPurpleLight else GlumeBorder),
                            modifier = Modifier.clickable { selectedVillage = v }
                        ) {
                            Text(
                                text = v,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else GlumeTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Urgent Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(if (isUrgent) "🚨" else "ℹ️", fontSize = 16.sp)
                        Text(
                            text = if (isUrgent) "High Priority Urgent Broadcast" else "Standard Village Notice",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUrgent) GlumeAlertCoral else GlumeTextPrimary
                            )
                        )
                    }

                    Switch(
                        checked = isUrgent,
                        onCheckedChange = { isUrgent = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlumeAlertCoral,
                            uncheckedTrackColor = GlumeSurfaceElevated
                        )
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xxs))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Please enter an advisory title."
                            return@Button
                        }
                        if (message.isBlank()) {
                            errorMessage = "Please enter the advisory message."
                            return@Button
                        }

                        val notice = BroadcastNotice(
                            id = "notice_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                            title = title.trim(),
                            message = message.trim(),
                            senderName = asha.name,
                            senderRole = UserRole.ASHA,
                            targetRole = "ALL",
                            targetVillage = if (selectedVillage == "All Villages") null else selectedVillage,
                            isUrgent = isUrgent,
                            timestamp = System.currentTimeMillis()
                        )

                        onSend(notice)
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUrgent) GlumeAlertCoral else GlumePrimaryPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "📢 Broadcast to Village Dashboard",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
