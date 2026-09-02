package com.vitalsense.app.core.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.ui.theme.AppLanguage
import java.util.*

object AudioGuidanceHelper {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun init(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isInitialized = true
                }
            }
        }
    }

    fun speak(context: Context, text: String, language: AppLanguage = AppLanguage.HINDI) {
        if (tts == null) {
            init(context)
        }

        try {
            val locale = if (language == AppLanguage.HINDI) Locale("hi", "IN") else Locale.US
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "VitalSenseAudioGuidance")
        } catch (e: Exception) {
            // Fallback gracefully
        }
    }

    fun provideHapticFeedback(context: Context, isSuccess: Boolean = true) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isSuccess) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    val timings = longArrayOf(0, 80, 60, 80)
                    val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isSuccess) 100 else 250)
            }
        } catch (e: Exception) {
            // Ignore if vibration unavailable
        }
    }

    fun getSpokenHealthSummary(
        patientName: String,
        severity: SeverityLevel,
        heartRate: Int = 76,
        spO2: Int = 98,
        language: AppLanguage = AppLanguage.HINDI
    ): String {
        val firstName = patientName.split(" ").firstOrNull() ?: patientName
        return if (language == AppLanguage.HINDI) {
            when (severity) {
                SeverityLevel.LOW -> 
                    "नमस्ते " + firstName + " जी। आज आपका स्वास्थ्य बिल्कुल ठीक है। दिल की धड़कन " + heartRate + " और ऑक्सीजन " + spO2 + " प्रतिशत सामान्य है।"
                SeverityLevel.MODERATE -> 
                    "नमस्ते " + firstName + " जी। कृपया अपने स्वास्थ्य पर ध्यान दें। अपने आशा कार्यकर्ता या डॉक्टर से परामर्श लें।"
                SeverityLevel.HIGH, SeverityLevel.SEVERE -> 
                    "सावधान " + firstName + " जी। तुरंत आपातकालीन सहायता लें या डॉक्टर से संपर्क करें।"
            }
        } else {
            when (severity) {
                SeverityLevel.LOW -> 
                    "Hello " + firstName + ". You are completely fine today. Your heart rate is " + heartRate + " and oxygen is " + spO2 + " percent, which is normal."
                SeverityLevel.MODERATE -> 
                    "Hello " + firstName + ". Please pay attention to your health. Consult your ASHA worker or doctor."
                SeverityLevel.HIGH, SeverityLevel.SEVERE -> 
                    "Warning " + firstName + ". Please seek immediate medical help or contact emergency services."
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
