package com.vitalsense.app.core.call

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.vitalsense.app.R
import java.util.concurrent.TimeUnit

class AppointmentReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val apptId = inputData.getString(KEY_APPOINTMENT_ID) ?: return Result.success()
        val doctorName = inputData.getString(KEY_DOCTOR_NAME) ?: "Doctor"
        val timeSlot = inputData.getString(KEY_TIME_SLOT) ?: "Soon"
        val minutesBefore = inputData.getInt(KEY_MINUTES_BEFORE, 15)

        showNotification(apptId, doctorName, timeSlot, minutesBefore)
        return Result.success()
    }

    private fun showNotification(
        apptId: String,
        doctorName: String,
        timeSlot: String,
        minutesBefore: Int
    ) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "appointment_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Consultation Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming video and voice consultations"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val title = "📅 " + applicationContext.getString(R.string.appointment_reminder_title)
        val message = applicationContext.getString(R.string.appointment_reminder_body, "Dr. $doctorName")

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(apptId.hashCode() + minutesBefore, notification)
    }

    companion object {
        const val KEY_APPOINTMENT_ID = "key_appt_id"
        const val KEY_DOCTOR_NAME = "key_doctor_name"
        const val KEY_TIME_SLOT = "key_time_slot"
        const val KEY_MINUTES_BEFORE = "key_minutes_before"

        /**
         * Schedules local notifications 30 min and 5 min before scheduled call.
         */
        fun scheduleReminders(
            context: Context,
            appointmentId: String,
            doctorName: String,
            timeSlot: String,
            scheduledTimestampMs: Long
        ) {
            val nowMs = System.currentTimeMillis()
            val workManager = WorkManager.getInstance(context)

            // 30 minutes before
            val delay30m = scheduledTimestampMs - nowMs - (30 * 60 * 1000L)
            if (delay30m > 0) {
                val data30 = workDataOf(
                    KEY_APPOINTMENT_ID to appointmentId,
                    KEY_DOCTOR_NAME to doctorName,
                    KEY_TIME_SLOT to timeSlot,
                    KEY_MINUTES_BEFORE to 30
                )
                val request30 = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(delay30m, TimeUnit.MILLISECONDS)
                    .setInputData(data30)
                    .build()
                workManager.enqueueUniqueWork(
                    "appt_reminder_${appointmentId}_30m",
                    ExistingWorkPolicy.REPLACE,
                    request30
                )
            }

            // 5 minutes before
            val delay5m = scheduledTimestampMs - nowMs - (5 * 60 * 1000L)
            if (delay5m > 0) {
                val data5 = workDataOf(
                    KEY_APPOINTMENT_ID to appointmentId,
                    KEY_DOCTOR_NAME to doctorName,
                    KEY_TIME_SLOT to timeSlot,
                    KEY_MINUTES_BEFORE to 5
                )
                val request5 = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(delay5m, TimeUnit.MILLISECONDS)
                    .setInputData(data5)
                    .build()
                workManager.enqueueUniqueWork(
                    "appt_reminder_${appointmentId}_5m",
                    ExistingWorkPolicy.REPLACE,
                    request5
                )
            }
        }
    }
}
