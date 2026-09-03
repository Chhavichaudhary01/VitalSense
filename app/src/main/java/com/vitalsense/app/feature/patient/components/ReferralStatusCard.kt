package com.vitalsense.app.feature.patient.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.Referral
import com.vitalsense.app.core.data.model.ReferralStatus
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper

@Composable
fun ReferralStatusCard(
    referral: Referral,
    language: AppLanguage,
    onScheduleCall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSpeaking by remember { mutableStateOf(false) }

    // Icon based on specialty
    val iconEmoji = when {
        referral.targetSpecialty.contains("Cardio", ignoreCase = true) -> "🩺 ➔ 🫀"
        referral.targetSpecialty.contains("Derma", ignoreCase = true) -> "🩺 ➔ 🔬"
        referral.targetSpecialty.contains("Pedia", ignoreCase = true) -> "🩺 ➔ 👶"
        referral.targetSpecialty.contains("Gynae", ignoreCase = true) || referral.targetSpecialty.contains("Maternal", ignoreCase = true) -> "🩺 ➔ 🌸"
        referral.targetSpecialty.contains("Ortho", ignoreCase = true) -> "🩺 ➔ 🦴"
        referral.targetSpecialty.contains("Psych", ignoreCase = true) || referral.targetSpecialty.contains("Mental", ignoreCase = true) -> "🩺 ➔ 🧠"
        else -> "🩺 ➔ 👨‍⚕️"
    }

    val (titleText, subtitleText, speakText) = when (referral.status) {
        ReferralStatus.SENT -> {
            if (language == AppLanguage.HINDI) {
                Triple(
                    "विशेषज्ञ डॉक्टर को रेफ़रल भेजा गया",
                    "आपके डॉक्टर ने आपके इलाज के लिए ${referral.targetSpecialty} विशेषज्ञ को अनुरोध भेजा है। विशेषज्ञ डॉक्टर जल्द समीक्षा करेंगे।",
                    "नमस्ते। आपके डॉक्टर ने आपको विशेषज्ञ के पास रेफ़र किया है। डॉक्टर जल्द ही आपकी रिपोर्ट देखकर संपर्क करेंगे।"
                )
            } else {
                Triple(
                    "Referred to Specialist Doctor",
                    "Your doctor has connected you with a ${referral.targetSpecialty} specialist for advanced care review.",
                    "Hello. Your doctor has referred you to a specialist for further evaluation. The specialist will review your case shortly."
                )
            }
        }
        ReferralStatus.ACCEPTED, ReferralStatus.IN_PROGRESS -> {
            if (language == AppLanguage.HINDI) {
                Triple(
                    "विशेषज्ञ ने आपका रेफ़रल स्वीकार किया",
                    "डॉक्टर ${referral.targetDoctorName ?: referral.targetSpecialty} ने समीक्षा स्वीकार कर ली है। जल्द ही आपसे परामर्श होगा।",
                    "विशेषज्ञ डॉक्टर ने आपका अनुरोध स्वीकार कर लिया है। जल्द ही आपसे वीडियो या वॉयस कॉल पर परामर्श होगा।"
                )
            } else {
                Triple(
                    "Specialist Accepted Your Case",
                    "Dr. ${referral.targetDoctorName ?: referral.targetSpecialty} has accepted your referral. Consultation will take place soon.",
                    "The specialist doctor has accepted your case. A consultation call will be initiated shortly."
                )
            }
        }
        ReferralStatus.COMPLETED -> {
            if (language == AppLanguage.HINDI) {
                Triple(
                    "विशेषज्ञ डॉक्टर की सलाह प्राप्त हुई",
                    referral.specialistRecommendations ?: "विशेषज्ञ की रिपोर्ट आपके डॉक्टर तक पहुंच गई है।",
                    "विशेषज्ञ डॉक्टर की रिपोर्ट और सलाह मिल गई है। आपका इलाज जारी है।"
                )
            } else {
                Triple(
                    "Specialist Advice & Plan Received",
                    referral.specialistRecommendations ?: "The specialist has closed the loop with your primary doctor.",
                    "Specialist advice and recommendations have been received and added to your health chart."
                )
            }
        }
        ReferralStatus.INFO_REQUESTED -> {
            if (language == AppLanguage.HINDI) {
                Triple(
                    "डॉक्टर आपकी रिपोर्ट की जांच कर रहे हैं",
                    "विशेषज्ञ ने अतिरिक्त विवरण मांगे हैं। आपके डॉक्टर इसे अपडेट कर रहे हैं।",
                    "डॉक्टर आपकी जांच रिपोर्ट का मिलान कर रहे हैं।"
                )
            } else {
                Triple(
                    "Doctors Reviewing Records",
                    "The specialist requested additional diagnostic details from your doctor.",
                    "Doctors are reviewing your medical investigation reports."
                )
            }
        }
        ReferralStatus.DECLINED -> {
            if (language == AppLanguage.HINDI) {
                Triple(
                    "रेफ़रल का पुनः निर्धारण",
                    "इस विभाग में सीट उपलब्ध न होने के कारण दूसरे विशेषज्ञ को भेजा जा रहा है।",
                    "दूसरे विशेषज्ञ डॉक्टर से संपर्क किया जा रहा है।"
                )
            } else {
                Triple(
                    "Referral Rerouting",
                    "Rerouting to alternate specialist department.",
                    "Your referral is being reassigned to another specialist."
                )
            }
        }
        else -> {
            Triple("Specialist Referral", "Active clinical coordination", "Active referral in progress")
        }
    }

    VitalSenseCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = if (referral.status == ReferralStatus.COMPLETED) GlumeSuccessContainer.copy(alpha = 0.25f) else GlumePrimaryPurpleContainer.copy(alpha = 0.35f),
        border = BorderStroke(
            1.5.dp,
            if (referral.status == ReferralStatus.COMPLETED) GlumeSuccessMint else GlumePrimaryPurple.copy(alpha = 0.6f)
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            // Header Row: Icon + Title + Audio Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GlumeSurfaceElevated,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = iconEmoji, fontSize = 16.sp)
                        }
                    }
                    Column {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "${referral.targetSpecialty} · Dr. ${referral.referringDoctorName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GlumeTextSecondary
                        )
                    }
                }

                // Audio Narration Button
                IconButton(
                    onClick = {
                        isSpeaking = true
                        AudioGuidanceHelper.speak(
                            context = context,
                            text = speakText,
                            language = language
                        )
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(if (isSpeaking) "🔊" else "🔈", fontSize = 20.sp)
                }
            }

            // Subtitle / Plain Language Explanation
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.bodySmall,
                color = GlumeTextPrimary
            )

            // Status Badge & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = PillShape,
                    color = when (referral.status) {
                        ReferralStatus.COMPLETED -> GlumeSuccessMint.copy(alpha = 0.2f)
                        ReferralStatus.ACCEPTED, ReferralStatus.IN_PROGRESS -> GlumePrimaryPurpleContainer
                        else -> GlumeWarningContainer
                    }
                ) {
                    Text(
                        text = when (referral.status) {
                            ReferralStatus.COMPLETED -> if (language == AppLanguage.HINDI) "✓ पूर्ण (सलाह दर्ज)" else "✓ Completed"
                            ReferralStatus.ACCEPTED -> if (language == AppLanguage.HINDI) "स्वीकार किया गया" else "Accepted"
                            ReferralStatus.SENT -> if (language == AppLanguage.HINDI) "समीक्षा जारी" else "Pending Review"
                            else -> referral.status.displayName
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = when (referral.status) {
                                ReferralStatus.COMPLETED -> GlumeSuccessMint
                                ReferralStatus.ACCEPTED -> GlumePrimaryPurpleLight
                                else -> GlumeWarningAmber
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (referral.status in listOf(ReferralStatus.ACCEPTED, ReferralStatus.IN_PROGRESS)) {
                    Button(
                        onClick = onScheduleCall,
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("📹 Join / Call", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }
            }
        }
    }
}
