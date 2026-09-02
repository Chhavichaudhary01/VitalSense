status_halo_code = '''package com.vitalsense.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper

/**
 * The Status Halo (Hero Element ~40% of screen) as specified in VitalSense_UX_Architecture.md §3.2
 * Provides instant glanceable health verdict + 2x2 vital tiles + Voice Narration.
 */
@Composable
fun StatusHaloCard(
    patient: Patient,
    heartRate: Int = 76,
    spO2: Int = 98,
    bloodPressure: String = "120/80",
    temperature: String = "98.4°F",
    language: AppLanguage = AppLanguage.HINDI,
    onTakeReadingClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val firstName = patient.name.split(" ").firstOrNull() ?: patient.name

    // Dynamic Halo Ring Color based on risk level
    val haloColor by animateColorAsState(
        targetValue = when (patient.currentRiskLevel) {
            SeverityLevel.LOW -> Color(0xFF2E9E5B) // Green Safe
            SeverityLevel.MODERATE -> Color(0xFFE8A93B) // Amber Attention
            SeverityLevel.HIGH, SeverityLevel.SEVERE -> Color(0xFFD63B3B) // Red Emergency
        },
        animationSpec = tween(durationMillis = 600),
        label = "HaloColorAnim"
    )

    val verdictWord = when (patient.currentRiskLevel) {
        SeverityLevel.LOW -> if (language == AppLanguage.HINDI) "ठीक हैं" else "You're Fine"
        SeverityLevel.MODERATE -> if (language == AppLanguage.HINDI) "ध्यान दें" else "Pay Attention"
        SeverityLevel.HIGH, SeverityLevel.SEVERE -> if (language == AppLanguage.HINDI) "तुरंत मदद लें" else "Get Help Now"
    }

    val verdictSubtitle = when (patient.currentRiskLevel) {
        SeverityLevel.LOW -> if (language == AppLanguage.HINDI) "सभी स्वास्थ्य पैरामीटर सामान्य हैं" else "All vital signs are healthy"
        SeverityLevel.MODERATE -> if (language == AppLanguage.HINDI) "परामर्श की आवश्यकता हो सकती है" else "May require consultation"
        SeverityLevel.HIGH, SeverityLevel.SEVERE -> if (language == AppLanguage.HINDI) "तत्काल डॉक्टर संपर्क करें" else "Immediate consultation advised"
    }

    VitalSenseCard(
        backgroundColor = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, haloColor.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Header Bar: Greeting & Sync Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.HINDI) "नमस्ते,  जी 🙏" else "Hello,  👋",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " · ASHA: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sync Status Chip
                Surface(
                    shape = PillShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("☁️✓", fontSize = 11.sp, color = VitalSenseTealPrimary)
                        Text(
                            text = if (language == AppLanguage.HINDI) "सुरक्षित" else "Synced",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 1. THE STATUS HALO (Hero Circle Element)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(haloColor.copy(alpha = 0.12f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(134.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (patient.currentRiskLevel) {
                                SeverityLevel.LOW -> "🟢"
                                SeverityLevel.MODERATE -> "🟡"
                                SeverityLevel.HIGH, SeverityLevel.SEVERE -> "🔴"
                            },
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = verdictWord,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = haloColor
                        )
                        Text(
                            text = if (language == AppLanguage.HINDI) "(You're Fine)" else "(Normal)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = verdictSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // 2. 2x2 VITAL TILES (56dp+ minimum touch targets)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    // Tile 1: Heart Rate
                    VitalTile(
                        icon = "❤️",
                        label = if (language == AppLanguage.HINDI) "दिल की धड़कन" else "Heart Rate",
                        value = " bpm",
                        status = if (language == AppLanguage.HINDI) "सामान्य" else "Normal",
                        statusColor = VitalSenseTealPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Tile 2: SpO2 Oxygen
                    VitalTile(
                        icon = "🫁",
                        label = if (language == AppLanguage.HINDI) "ऑक्सीजन (SpO2)" else "Oxygen (SpO2)",
                        value = "%",
                        status = if (language == AppLanguage.HINDI) "सामान्य" else "Normal",
                        statusColor = VitalSenseTealPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    // Tile 3: Blood Pressure
                    VitalTile(
                        icon = "💧",
                        label = if (language == AppLanguage.HINDI) "रक्तचाप (BP)" else "Blood Pressure",
                        value = bloodPressure,
                        status = if (language == AppLanguage.HINDI) "सामान्य" else "Normal",
                        statusColor = VitalSenseTealPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Tile 4: Temperature
                    VitalTile(
                        icon = "🌡️",
                        label = if (language == AppLanguage.HINDI) "तापमान (Temp)" else "Temperature",
                        value = temperature,
                        status = if (language == AppLanguage.HINDI) "सामान्य" else "Normal",
                        statusColor = VitalSenseTealPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Bottom Action Bar: Audio Narration & Take a Reading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔊 "Listen to this" Audio Narration Button
                Surface(
                    shape = PillShape,
                    color = VitalSenseTealContainer,
                    modifier = Modifier.clickable {
                        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                        val speech = AudioGuidanceHelper.getSpokenHealthSummary(
                            patientName = patient.name,
                            severity = patient.currentRiskLevel,
                            heartRate = heartRate,
                            spO2 = spO2,
                            language = language
                        )
                        AudioGuidanceHelper.speak(context, speech, language)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔊", fontSize = 14.sp)
                        Text(
                            text = if (language == AppLanguage.HINDI) "सुनें (Listen)" else "Listen",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VitalSenseTealPrimary
                            )
                        )
                    }
                }

                // 🩺 Take a Reading Button
                Button(
                    onClick = onTakeReadingClick,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = VitalSenseTealPrimary),
                    contentPadding = PaddingValues(horizontal = Spacing.md, vertical = 6.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "🩺 नई रीडिंग लें" else "🩺 Take Reading",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun VitalTile(
    icon: String,
    label: String,
    value: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.defaultMinSize(minHeight = 56.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 16.sp)
                Surface(
                    shape = PillShape,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = statusColor
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
'''

with open('app/src/main/java/com/vitalsense/app/core/ui/components/StatusHaloCard.kt', 'w', encoding='utf-8') as f:
    f.write(status_halo_code)

print('Updated StatusHaloCard.kt')
