package com.vitalsense.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vitalsense.app.MainActivity
import com.vitalsense.app.R

class VitalSenseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "VitalSenseFCM"
        const val QUEUE_CHANNEL_ID = "vitalsense_queue_channel"
        const val QUEUE_CHANNEL_NAME = "Clinic Queue Alerts"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        // Token will be synced to user's profile on next session
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val type = data["type"] ?: "GENERAL"
        val title = remoteMessage.notification?.title ?: data["title"] ?: "VitalSense Queue Alert"
        val body = remoteMessage.notification?.body ?: data["body"] ?: "Your clinic queue status has been updated."

        showQueueNotification(type, title, body)
    }

    private fun showQueueNotification(type: String, title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = if (type == "QUEUE_CALLED") NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(QUEUE_CHANNEL_ID, QUEUE_CHANNEL_NAME, importance).apply {
                description = "Real-time updates regarding clinic queue positions and consultation calls"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("NOTIFICATION_TYPE", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isHighAlert = type == "QUEUE_CALLED"

        val notification = NotificationCompat.Builder(this, QUEUE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(if (isHighAlert) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = if (isHighAlert) 1001 else (System.currentTimeMillis() % 10000).toInt()
        notificationManager.notify(notificationId, notification)
    }
}
