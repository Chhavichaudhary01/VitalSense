package com.vitalsense.app.feature.prescriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vitalsense.app.core.data.model.Prescription
import com.vitalsense.app.core.ui.theme.*

@Composable
fun SharePrescriptionDialog(
    prescription: Prescription,
    patientPhoneDefault: String? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var targetPhone by remember { mutableStateOf(patientPhoneDefault ?: "") }

    val formattedText = remember(prescription, targetPhone) {
        PrescriptionShareHelper.formatPrescriptionText(
            prescription = prescription,
            patientPhone = targetPhone.ifBlank { null }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = VS_Surface,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, VS_Outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(VS_Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📲", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "Share e-Prescription",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VS_OnBackground
                            )
                            Text(
                                text = "SMS & WhatsApp Fallback for Rural Patients",
                                style = MaterialTheme.typography.bodySmall,
                                color = VS_OnSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = VS_OnSurfaceVariant)
                    }
                }

                HorizontalDivider(color = VS_Outline, modifier = Modifier.padding(vertical = 10.dp))

                // Patient Mobile Phone Field
                OutlinedTextField(
                    value = targetPhone,
                    onValueChange = { targetPhone = it },
                    label = { Text("Recipient Phone Number (SMS / WhatsApp)") },
                    placeholder = { Text("+91 98765 43210") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VS_Primary,
                        unfocusedBorderColor = VS_Outline,
                        focusedTextColor = VS_OnBackground,
                        unfocusedTextColor = VS_OnBackground
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Preview Card
                Text(
                    text = "Message Preview (Standard Text / SMS Format):",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = VS_OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VS_SurfaceVariant.copy(alpha = 0.7f))
                        .border(1.dp, VS_Outline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = formattedText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        ),
                        color = VS_OnBackground
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Grid (WhatsApp & SMS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            PrescriptionShareHelper.shareViaWhatsApp(
                                context = context,
                                text = formattedText,
                                phoneNumber = targetPhone.ifBlank { null }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        ),
                        shape = PillShape,
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("💬", fontSize = 15.sp)
                            Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = {
                            PrescriptionShareHelper.shareViaSms(
                                context = context,
                                text = formattedText,
                                phoneNumber = targetPhone.ifBlank { null }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VS_Primary,
                            contentColor = Color.White
                        ),
                        shape = PillShape,
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("✉️", fontSize = 15.sp)
                            Text("Send SMS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            PrescriptionShareHelper.copyToClipboard(context, formattedText)
                        },
                        shape = PillShape,
                        border = BorderStroke(1.dp, VS_Outline),
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 38.dp)
                    ) {
                        Text("📋 Copy Text", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = VS_OnBackground)
                    }

                    OutlinedButton(
                        onClick = {
                            PrescriptionShareHelper.shareGeneral(context, formattedText)
                        },
                        shape = PillShape,
                        border = BorderStroke(1.dp, VS_Outline),
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 38.dp)
                    ) {
                        Text("🔗 More Apps", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = VS_OnBackground)
                    }
                }
            }
        }
    }
}
