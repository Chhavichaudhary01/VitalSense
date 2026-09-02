package com.vitalsense.app.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.local.seed.SeedDataProvider
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseDialog
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper
import kotlinx.coroutines.delay

/**
 * ASHA-Assisted QR Claim Onboarding Flow
 * as specified in VitalSense_UX_Architecture.md §1.4.
 *
 * Allows rural patients without typing literacy to claim their health profile
 * by scanning their ASHA-issued physical health card QR matrix.
 */
@Composable
fun AshaQrClaimDialog(
    language: AppLanguage = AppLanguage.HINDI,
    onDismiss: () -> Unit,
    onPatientClaimed: (Patient) -> Unit
) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(true) }
    var claimedPatient by remember { mutableStateOf<Patient?>(null) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            AudioGuidanceHelper.speak(
                context = context,
                text = if (language == AppLanguage.HINDI) "आशा स्वास्थ्य कार्ड का क्यूआर कोड कैमरे के सामने लाएं..." else "Align the ASHA Health Card QR in front of the camera...",
                language = language
            )
            delay(2400)
            AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
            // Claim sample patient Ramesh Kumar
            claimedPatient = SeedDataProvider.initialPatients.first()
            isScanning = false
        }
    }

    VitalSenseDialog(
        onDismissRequest = onDismiss,
        title = if (language == AppLanguage.HINDI) "🪪 आशा स्वास्थ्य कार्ड स्कैन करें" else "🪪 Scan ASHA Health Card",
        icon = { Text("🪪", fontSize = 22.sp) },
        confirmButton = {
            if (!isScanning && claimedPatient != null) {
                Button(
                    onClick = {
                        onPatientClaimed(claimedPatient!!)
                        onDismiss()
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumeSuccessMint),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.HINDI) "✓ प्रोफ़ाइल से जुड़ें (Claim Profile)" else "✓ Claim & Enter Profile",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = GlumeBackground
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = PillShape) {
                Text(
                    text = if (language == AppLanguage.HINDI) "रद्द करें" else "Cancel",
                    color = GlumeTextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            if (isScanning) {
                // QR Scanning Camera Viewport Simulation
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CardShape)
                        .background(GlumeSurfaceElevated)
                        .padding(Spacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📷", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        Text(
                            text = "Scanning ASHA QR...",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumePrimaryPurpleLight
                        )
                    }
                }

                Text(
                    text = if (language == AppLanguage.HINDI) "आशा कार्यकर्ता द्वारा दिया गया स्वास्थ्य कार्ड स्कैन करें। किसी पासवर्ड की आवश्यकता नहीं है।" else "Scan the physical health card issued by your village ASHA worker. Zero passwords required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlumeTextSecondary
                )
            } else if (claimedPatient != null) {
                // Verified Patient Identity
                val patient = claimedPatient!!
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
                            text = if (language == AppLanguage.HINDI) "मरीज़ पहचान सत्यापित!" else "Patient Identity Verified!",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeSuccessMint
                        )
                    }
                }

                VitalSenseCard(
                    backgroundColor = GlumeSurfaceElevated,
                    border = BorderStroke(1.dp, GlumeSuccessMint.copy(alpha = 0.4f))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = patient.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "गांव:  · आयु:  ()",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                        Text(
                            text = "आशा कार्यकर्ता: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumePrimaryPurpleLight
                        )
                    }
                }
            }
        }
    }
}
