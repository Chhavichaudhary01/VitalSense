package com.vitalsense.app.feature.prescriptions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.vitalsense.app.core.data.model.PrescribedMedicine
import com.vitalsense.app.core.data.model.Prescription
import com.vitalsense.app.core.util.AudioGuidanceHelper
import java.net.URLEncoder

object PrescriptionShareHelper {

    fun formatPrescriptionText(
        prescription: Prescription,
        patientPhone: String? = null
    ): String {
        return formatPrescriptionText(
            patientName = prescription.patientName,
            patientPhone = patientPhone,
            doctorName = prescription.doctorName,
            doctorSpecialty = prescription.doctorSpecialty,
            dateFormatted = prescription.dateFormatted,
            medicines = prescription.medicines,
            instructions = prescription.instructions
        )
    }

    fun formatPrescriptionText(
        patientName: String,
        patientPhone: String? = null,
        doctorName: String,
        doctorSpecialty: String,
        dateFormatted: String,
        medicines: List<PrescribedMedicine>,
        instructions: String
    ): String {
        val sb = StringBuilder()
        sb.append("📋 *SEHAT SETU / VITALSENSE e-PRESCRIPTION*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👤 *Patient*: $patientName\n")
        if (!patientPhone.isNullOrBlank()) {
            sb.append("📞 *Phone*: $patientPhone\n")
        }
        sb.append("👨‍⚕️ *Doctor*: $doctorName ($doctorSpecialty)\n")
        sb.append("📅 *Date*: $dateFormatted\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")

        sb.append("💊 *PRESCRIBED MEDICINES*:\n")
        if (medicines.isEmpty()) {
            sb.append("• See general medical instructions below\n")
        } else {
            medicines.forEachIndexed { idx, med ->
                sb.append("${idx + 1}. *${med.name}* (${med.dosage})\n")
                sb.append("   ⏰ Timing: ${med.frequency}\n")
                sb.append("   ⏳ Duration: ${med.duration}")
                if (med.quantity > 0) {
                    sb.append(" | Qty: ${med.quantity}")
                }
                sb.append("\n")
            }
        }

        if (instructions.isNotBlank()) {
            sb.append("\n📝 *INSTRUCTIONS & ADVICE*:\n")
            sb.append("$instructions\n")
        }

        sb.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🚨 *Emergency Helpline*: 108 (Ambulance) | 104 (Health Info)\n")
        sb.append("🏥 Govt PHC Digital Health Record | Verified on SehatSetu")
        return sb.toString()
    }

    fun shareViaWhatsApp(context: Context, text: String, phoneNumber: String? = null) {
        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
        try {
            val cleanPhone = phoneNumber?.replace("[^0-9]".toRegex(), "") ?: ""
            val url = if (cleanPhone.length >= 10) {
                val fullNumber = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
                "https://api.whatsapp.com/send?phone=$fullNumber&text=${URLEncoder.encode(text, "UTF-8")}"
            } else {
                "https://api.whatsapp.com/send?text=${URLEncoder.encode(text, "UTF-8")}"
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // If WhatsApp package is not installed directly, fallback to chooser
            shareGeneral(context, text)
        }
    }

    fun shareViaSms(context: Context, text: String, phoneNumber: String? = null) {
        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
        try {
            val uri = if (!phoneNumber.isNullOrBlank()) {
                val cleanPhone = phoneNumber.replace("[^0-9+]".toRegex(), "")
                Uri.parse("smsto:$cleanPhone")
            } else {
                Uri.parse("smsto:")
            }

            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            shareGeneral(context, text)
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("SehatSetu Prescription", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "✓ Prescription copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not copy: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareGeneral(context: Context, text: String) {
        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "SehatSetu e-Prescription")
        }
        val chooser = Intent.createChooser(sendIntent, "Share Prescription via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
