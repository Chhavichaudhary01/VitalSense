package com.vitalsense.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VitalSenseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("VitalSenseFirebase", "FirebaseApp initialized successfully in VitalSenseApp")
        } catch (e: Exception) {
            Log.e("VitalSenseFirebase", "FirebaseApp initialization error: ${e.message}", e)
        }
    }
}
