package com.vitalsense.app.feature.patient.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.VitalSenseDialog
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper
import com.vitalsense.app.core.util.EmergencySosHelper
import kotlinx.coroutines.delay

/**
 * Step-by-Step Bluetooth Sensor Pairing & Live Waveform Flow
 * as specified in VitalSense_UX_Architecture.md §3.3 & §5.3.
 */
@Composable
fun SensorPairingDialog(
    patient: Patient,
    language: AppLanguage = AppLanguage.HINDI,
    onDismiss: () -> Unit,
    onReadingCaptured: (heartRate: Int, spO2: Int, bp: String, temp: String) -> Unit
) {
    val context = LocalContext.current

    // Steps: 1 = Position Finger Clip, 2 = Search & Pair Bluetooth, 3 = Live Reading Capture, 4 = Result Done
    var currentStep by remember { mutableStateOf(1) }

    // Pulse animation for Bluetooth search and Heartbeat
    val infiniteTransition = rememberInfiniteTransition(label = "SensorPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Measured simulated vitals
    var liveHeartRate by remember { mutableStateOf(72) }
    var liveSpO2 by remember { mutableStateOf(98) }

    // Step 2 & 3 Auto-simulation
    LaunchedEffect(currentStep) {
        if (currentStep == 2) {
            AudioGuidanceHelper.speak(
                context = context,
                text = if (language == AppLanguage.HINDI) "ब्लूटूथ सेंसर से कनेक्ट किया जा रहा है..." else "Connecting to Bluetooth sensor...",
                language = language
            )
            delay(2800)
            AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
            currentStep = 3
        } else if (currentStep == 3) {
            AudioGuidanceHelper.speak(
                context = context,
                text = if (language == AppLanguage.HINDI) "रीडिंग ली जा रही है। कृपया शांत बैठें।" else "Capturing live vitals. Please stay still.",
                language = language
            )
            for (i in 1..4) {
                delay(700)
                liveHeartRate = (72..78).random()
                liveSpO2 = (98..99).random()
            }
            delay(500)
            AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
            currentStep = 4
        }
    }

    VitalSenseDialog(
        onDismissRequest = onDismiss,
        title = if (language == AppLanguage.HINDI) "🩺 ब्लूटूथ स्वास्थ्य सेंसर" else "🩺 Health Sensor",
        icon = { Text("🩺", fontSize = 22.sp) },
        confirmButton = {
            if (currentStep == 1) {
                Button(
                    onClick = {
                        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                        currentStep = 2
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "सेंसर खोजें (Step 2)" else "Search Sensor",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            } else if (currentStep == 4) {
                Button(
                    onClick = {
                        onReadingCaptured(liveHeartRate, liveSpO2, "120/80", "98.4°F")
                        onDismiss()
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumeSuccessMint),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "✓ स्वास्थ्य रिकॉर्ड में सहेजें" else "✓ Save to Health Record",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = GlumeBackground
                    )
                }
            }
        },
        dismissButton = {
            if (currentStep != 4) {
                TextButton(onClick = onDismiss, shape = PillShape) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "रद्द करें" else "Cancel",
                        color = GlumeTextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // 1. Progress Step Dots (●●○)
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.HINDI) "चरण  / 3:" else "Step  of 3:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GlumePrimaryPurpleLight
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..3) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i <= currentStep) GlumePrimaryPurple else GlumeBorder
                                )
                        )
                    }
                }
            }

            HorizontalDivider(color = GlumeBorder)

            // Step Content
            when (currentStep) {
                1 -> {
                    // STEP 1: Position Finger Clip
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(
                            text = if (language == AppLanguage.HINDI) "अपनी उंगली में क्लिप लगाएं" else "Put the clip on your finger",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )

                        // Animated Instruction Visual Box
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CardShape)
                                .background(GlumeSurfaceElevated)
                                .padding(Spacing.sm),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👆📎", fontSize = 48.sp)
                        }

                        Text(
                            text = if (language == AppLanguage.HINDI) "सेंसर क्लिप को तर्जनी उंगली में सुरक्षित लगाएं और बटन चालू करें।" else "Attach the pulse oximeter clip to your index finger and turn it on.",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                }

                2 -> {
                    // STEP 2: Bluetooth Search & Connection
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(
                            text = if (language == AppLanguage.HINDI) "📶 सेंसर खोजा जा रहा है..." else "📶 Searching for sensor...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(GlumePrimaryPurpleContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp * pulseScale)
                                    .clip(CircleShape)
                                    .background(GlumePrimaryPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📡", fontSize = 32.sp)
                            }
                        }

                        Text(
                            text = "VitalSense BLE Smart Band v2.4",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumePrimaryPurpleLight
                        )
                    }
                }

                3 -> {
                    // STEP 3: Real-Time Waveform & Vitals Drawing
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(
                            text = if (language == AppLanguage.HINDI) "💓 लाइव धड़कन मापी जा रही है..." else "💓 Reading live heartbeat...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(CardShape)
                                .background(GlumeSurfaceElevated)
                                .padding(Spacing.sm),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("❤️", fontSize = 20.sp)
                                    Text(
                                        text = " bpm",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeAlertCoral
                                    )
                                }
                                Text("〰️〰️📈〰️〰️", fontSize = 22.sp, color = GlumeSuccessMint)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🫁", fontSize = 20.sp)
                                    Text(
                                        text = "%",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GlumePrimaryPurpleLight
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (language == AppLanguage.HINDI) "कृपया हिलें-डुलें नहीं। रीडिंग स्थिर हो रही है..." else "Please stay still. Stabilizing reading...",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                }

                4 -> {
                    // STEP 4: Success Result
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GlumeSuccessContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✓", fontSize = 28.sp, color = GlumeSuccessMint, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = if (language == AppLanguage.HINDI) "✅ रीडिंग सफलतापूर्वक पूरी हुई!" else "✅ Reading Completed Successfully!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeSuccessMint
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Surface(
                                shape = CardShape,
                                color = GlumeSurfaceElevated,
                                border = BorderStroke(1.dp, GlumeBorder),
                                modifier = Modifier.weight(1f).padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.xs),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("❤️ Heart Rate", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                                    Text(" bpm", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                                }
                            }

                            Surface(
                                shape = CardShape,
                                color = GlumeSurfaceElevated,
                                border = BorderStroke(1.dp, GlumeBorder),
                                modifier = Modifier.weight(1f).padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.xs),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🫁 SpO2 Oxygen", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                                    Text("%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Always Present Escape Hatch: "Need help? Call ASHA Worker"
            HorizontalDivider(color = GlumeBorder.copy(alpha = 0.5f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        EmergencySosHelper.dialEmergencyCall(context, patient.emergencyContact)
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👩‍⚕️ ", fontSize = 16.sp)
                Text(
                    text = if (language == AppLanguage.HINDI) "मदद चाहिए? आशा कार्यकर्ता को कॉल करें ()" else "Need help? Call ASHA Worker ()",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GlumePrimaryPurpleLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
