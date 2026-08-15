package com.vitalsense.app.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.vitalsense.app.core.data.model.Patient

object EmergencySosHelper {

    /**
     * Composes emergency SMS message containing GPS coordinates and patient details
     */
    fun createSosMessage(patient: Patient, latitude: Double = 28.6139, longitude: Double = 77.2090): String {
        return "🚨 [VITALSENSE EMERGENCY SOS] 🚨\n" +
                "Patient: ${patient.name} (Age: ${patient.age}, Gender: ${patient.gender})\n" +
                "Village: ${patient.villageName}\n" +
                "Phone: ${patient.phone}\n" +
                "GPS Location: https://maps.google.com/?q=$latitude,$longitude\n" +
                "High-Priority Medical Assistance Required Immediately!"
    }

    /**
     * Launches cellular SMS app pre-populated with emergency text and recipient number
     */
    fun sendCellularSmsFallback(
        context: Context,
        recipientPhone: String,
        message: String
    ) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$recipientPhone")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback generic SMS intent
            val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$recipientPhone")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(genericIntent)
        }
    }

    /**
     * Launches phone dialer for 108 ambulance or ASHA phone
     */
    fun dialEmergencyCall(context: Context, phoneNumber: String = "108") {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Log fallback
        }
    }
}
