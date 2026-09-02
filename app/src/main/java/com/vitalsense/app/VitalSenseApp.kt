package com.vitalsense.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VitalSenseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Shield against background thread unhandled crashes while the app is running
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("VitalSenseCrashShield", "Uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)
            if (thread.name.contains("main", ignoreCase = true)) {
                defaultHandler?.uncaughtException(thread, throwable)
            } else {
                // Recover gracefully from transient background thread exceptions
                Log.w("VitalSenseCrashShield", "Background thread exception suppressed to prevent application crash: ${throwable.message}")
            }
        }

        try {
            FirebaseApp.initializeApp(this)
            Log.d("VitalSenseFirebase", "FirebaseApp initialized successfully in VitalSenseApp")
            com.vitalsense.app.core.sync.SyncManager(this).schedulePeriodicSync()
        } catch (e: Exception) {
            Log.e("VitalSenseFirebase", "FirebaseApp initialization error: ${e.message}", e)
        }
    }
}
