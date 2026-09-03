package com.vitalsense.app.core.call

import com.vitalsense.app.core.data.model.Appointment
import java.text.SimpleDateFormat
import java.util.*

enum class JoinWindowStatus {
    BEFORE_WINDOW,        // > 10 min before scheduled time (show scheduled time & reschedule button)
    JOIN_ACTIVE,          // -10 min to +15 min grace window (Join Call is active)
    AFTER_WINDOW_MISSED   // > 15 min after scheduled time (No-show, mark missed, prompt reschedule)
}

object AppointmentScheduleHelper {

    private const val GRACE_BEFORE_MS = 10 * 60 * 1000L // 10 minutes before
    private const val GRACE_AFTER_MS = 15 * 60 * 1000L  // 15 minutes after

    /**
     * Evaluates the current join window status for a scheduled appointment.
     */
    fun evaluateJoinWindow(
        appointment: Appointment,
        currentTimeMs: Long = System.currentTimeMillis()
    ): JoinWindowStatus {
        val scheduledTimeMs = getScheduledTimestamp(appointment)
        val diff = currentTimeMs - scheduledTimeMs

        return when {
            diff < -GRACE_BEFORE_MS -> JoinWindowStatus.BEFORE_WINDOW
            diff <= GRACE_AFTER_MS -> JoinWindowStatus.JOIN_ACTIVE
            else -> JoinWindowStatus.AFTER_WINDOW_MISSED
        }
    }

    /**
     * Resolves or parses the timestamp for an appointment.
     */
    fun getScheduledTimestamp(appointment: Appointment): Long {
        if (appointment.scheduledTimestamp > 0L) {
            return appointment.scheduledTimestamp
        }

        // Parse dateFormatted & timeSlot
        return try {
            val dateStr = when (appointment.dateFormatted.lowercase()) {
                "today" -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                "tomorrow" -> {
                    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                }
                else -> appointment.dateFormatted
            }

            // Extract first time if range e.g. "10:00 AM - 10:30 AM" or "10:00 AM"
            val timePart = appointment.timeSlot.split("-").first().trim()
            val fullStr = "$dateStr $timePart"

            val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
            format.parse(fullStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * Returns human-readable time remaining or status text for the appointment button.
     */
    fun getJoinWindowDisplayText(
        appointment: Appointment,
        currentTimeMs: Long = System.currentTimeMillis()
    ): String {
        val scheduledTimeMs = getScheduledTimestamp(appointment)
        val diff = scheduledTimeMs - currentTimeMs

        return when (evaluateJoinWindow(appointment, currentTimeMs)) {
            JoinWindowStatus.BEFORE_WINDOW -> {
                val minsUntil = (diff / (60 * 1000L)).coerceAtLeast(1)
                if (minsUntil > 60) {
                    "Scheduled for ${appointment.timeSlot}"
                } else {
                    "Starts in ${minsUntil}m (Join opens 10m prior)"
                }
            }
            JoinWindowStatus.JOIN_ACTIVE -> {
                if (appointment.callType.name == "VOICE") "🎙️ Join Voice Consultation" else "📹 Join Video Consultation"
            }
            JoinWindowStatus.AFTER_WINDOW_MISSED -> {
                "Missed · Rebook Appointment"
            }
        }
    }

    /**
     * Generates a unique, secure Jitsi Meet room name for this appointment consultation.
     */
    fun generateJitsiRoomUrl(appointmentId: String): String {
        val cleanId = appointmentId.replace("[^a-zA-Z0-9]".toRegex(), "")
        return "https://meet.jit.si/VitalSense-Consultation-$cleanId"
    }

    /**
     * Generates an emergency Jitsi Meet room name.
     */
    fun generateEmergencyRoomUrl(patientId: String): String {
        val cleanId = patientId.replace("[^a-zA-Z0-9]".toRegex(), "")
        val timeId = (System.currentTimeMillis() / 10000)
        return "https://meet.jit.si/VitalSense-EMERGENCY-$cleanId-$timeId"
    }
}
