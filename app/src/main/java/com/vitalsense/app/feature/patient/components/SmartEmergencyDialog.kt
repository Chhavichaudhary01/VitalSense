package com.vitalsense.app.feature.patient.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitalsense.app.core.call.*
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseDialog
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper
import com.vitalsense.app.core.util.EmergencySosHelper
import com.vitalsense.app.feature.doctor.components.TeleConsultationModal
import kotlinx.coroutines.delay

/**
 * Smart Emergency Screen with 3-Second Countdown & Auto-GPS/SMS Dispatch
 * and Immediate On-Call Video / Voice Routing.
 */
@Composable
fun SmartEmergencyDialog(
    patient: Patient,
    language: AppLanguage = AppLanguage.HINDI,
    assignedDoctor: Doctor? = null,
    onCallDoctors: List<Doctor> = emptyList(),
    onDismiss: () -> Unit,
    onSosDispatched: () -> Unit = {}
) {
    val context = LocalContext.current
    val activeSession by TeleCallingManager.currentSession.collectAsStateWithLifecycle()

    // Countdown state: 3 -> 2 -> 1 -> 0 (Triggered)
    var countdownSeconds by remember { mutableStateOf(3) }
    var isCountdownActive by remember { mutableStateOf(true) }

    val sosMessage = remember {
        EmergencySosHelper.createSosMessage(patient)
    }

    // 3-second active countdown loop
    LaunchedEffect(isCountdownActive) {
        if (isCountdownActive) {
            while (countdownSeconds > 0) {
                AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = false)
                AudioGuidanceHelper.speak(
                    context = context,
                    text = "",
                    language = language
                )
                delay(1000)
                countdownSeconds -= 1
            }
            // Auto dispatch on countdown zero
            isCountdownActive = false
            onSosDispatched()
            
            // Auto background SMS with GPS coordinates
            EmergencySosHelper.sendCellularSmsFallback(
                context = context,
                recipientPhone = patient.emergencyContact,
                message = sosMessage
            )
        }
    }

    VitalSenseDialog(
        onDismissRequest = {
            isCountdownActive = false
            onDismiss()
        },
        title = if (language == AppLanguage.HINDI) "🚨 आपातकालीन सहायता (SOS)" else "🚨 Emergency SOS",
        icon = { Text("🚨", fontSize = 24.sp) },
        confirmButton = {
            if (isCountdownActive) {
                // Large CANCEL button during countdown
                Button(
                    onClick = {
                        isCountdownActive = false
                        onDismiss()
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumeSurfaceElevated),
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 46.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "✕ अभी रोकें (Cancel Alert)" else "✕ Cancel Alert",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )
                }
            } else {
                Button(
                    onClick = onDismiss,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "समझ गया (Close)" else "Close",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {}
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            if (isCountdownActive) {
                // 1. COUNTDOWN VIEW
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "आपातकालीन अलर्ट भेजा जा रहा है:" else "Emergency Alert Sending in:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GlumeAlertCoral
                    )

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(GlumeAlertCoral.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 44.sp
                            ),
                            color = GlumeAlertCoral
                        )
                    }

                    Text(
                        text = if (language == AppLanguage.HINDI) "गलती से दबा? 'अभी रोकें' बटन दबाएं।" else "Accidental press? Tap Cancel below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )
                }
            } else {
                // 2. DISPATCHED CONFIRMATION & DUAL GIANT BUTTONS
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Surface(
                        shape = PillShape,
                        color = GlumeSuccessContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("✓ ", color = GlumeSuccessMint, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (language == AppLanguage.HINDI) "सहायता रास्ते में है! (Help is on the way)" else "Help is on the way!",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeSuccessMint
                            )
                        }
                    }

                    // Static GPS Location Pin Card
                    VitalSenseCard(
                        backgroundColor = GlumeSurfaceElevated,
                        border = BorderStroke(1.dp, GlumeBorder)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text("📍", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "GPS Coordinates Shared with 108 & ASHA",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = " · Lat 28.6139, Long 77.2090",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GlumeTextSecondary
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = GlumeBorder)

                    // Live Emergency Call Routing State
                    if (activeSession != null && activeSession?.mode == CallMode.EMERGENCY) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = GlumeAlertContainer,
                            border = BorderStroke(1.5.dp, GlumeAlertCoral),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🚨", fontSize = 20.sp)
                                    Text(
                                        text = "EMERGENCY CALL IN PROGRESS",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeAlertText
                                    )
                                }
                                Text(
                                    text = activeSession?.statusMessage ?: "Connecting…",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = "Target: Dr. ${activeSession?.doctorName} (${activeSession?.doctorSpecialty})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (activeSession?.type == CallType.VIDEO) {
                                        OutlinedButton(
                                            onClick = { TeleCallingManager.switchToVoice() },
                                            shape = PillShape,
                                            modifier = Modifier.weight(1f).height(40.dp)
                                        ) {
                                            Text("Switch to Voice", style = MaterialTheme.typography.labelSmall, color = GlumeWarningAmber)
                                        }
                                    }
                                    Button(
                                        onClick = { TeleCallingManager.endCall("Emergency call cancelled by user") },
                                        shape = PillShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = GlumeSurfaceElevated),
                                        border = BorderStroke(1.dp, GlumeBorder),
                                        modifier = Modifier.weight(1f).height(40.dp)
                                    ) {
                                        Text("End Call", style = MaterialTheme.typography.labelSmall, color = GlumeTextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // EMERGENCY CALL BUTTON 1: Giant Voice Call Now (Default / Low-Bandwidth primary)
                    Button(
                        onClick = {
                            TeleCallingManager.startEmergencyCall(
                                context = context,
                                patient = patient,
                                callType = CallType.VOICE,
                                assignedDoctor = assignedDoctor,
                                onCallDoctors = onCallDoctors
                            )
                        },
                        shape = CardShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GlumeSuccessMint),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text("🎙️", fontSize = 20.sp)
                            Text(
                                text = if (language == AppLanguage.HINDI) "🚨 आपातकालीन वॉयस कॉल (On-Call SOS)" else "🚨 Emergency Voice Call (On-Call SOS)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeBackground
                            )
                        }
                    }

                    // EMERGENCY CALL BUTTON 2: Giant Video Call Now
                    Button(
                        onClick = {
                            TeleCallingManager.startEmergencyCall(
                                context = context,
                                patient = patient,
                                callType = CallType.VIDEO,
                                assignedDoctor = assignedDoctor,
                                onCallDoctors = onCallDoctors
                            )
                        },
                        shape = CardShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GlumeAlertCoral),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text("🎥", fontSize = 20.sp)
                            Text(
                                text = if (language == AppLanguage.HINDI) "🚨 आपातकालीन वीडियो कॉल (Video SOS)" else "🚨 Emergency Video Call (Immediate SOS)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // BUTTON 3: Giant Red "Call for Help Now (108)" (72dp height touch target)
                    Button(
                        onClick = {
                            EmergencySosHelper.dialEmergencyCall(context, "108")
                        },
                        shape = CardShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GlumeAlertCoral.copy(alpha = 0.85f)),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text("📞 108", fontSize = 18.sp, color = Color.White)
                            Text(
                                text = if (language == AppLanguage.HINDI) "तुरंत 108 एम्बुलेंस कॉल करें" else "Call 108 Ambulance Now",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // BUTTON 4: Giant Orange "Call ASHA" (52dp height touch target)
                    Button(
                        onClick = {
                            EmergencySosHelper.dialEmergencyCall(context, patient.emergencyContact)
                        },
                        shape = CardShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text("👩‍⚕️", fontSize = 18.sp)
                            Text(
                                text = if (language == AppLanguage.HINDI) "आशा कार्यकर्ता को कॉल करें" else "Call ASHA Worker",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Active in-call modal for emergency
    if (activeSession?.state == CallSessionState.CONNECTED) {
        TeleConsultationModal(
            patientName = patient.name,
            doctorName = activeSession?.doctorName ?: "Doctor",
            specialty = activeSession?.doctorSpecialty ?: "Emergency On-Call",
            villageName = patient.villageName,
            patientAge = patient.age,
            onDismiss = {
                TeleCallingManager.endCall("Patient ended call")
            },
            onEndCall = {
                TeleCallingManager.endCall("Emergency consultation finished")
            }
        )
    }
}
