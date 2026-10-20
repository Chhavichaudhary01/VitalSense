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

    fun speak(context: Context, text: String, language: AppLanguage = AppLanguage.ENGLISH) {
        if (tts == null) {
            init(context)
        }

        try {
            val locale = when (language) {
                AppLanguage.ENGLISH -> Locale.US
                AppLanguage.HINDI -> Locale("hi", "IN")
                AppLanguage.TAMIL -> Locale("ta", "IN")
                AppLanguage.MARATHI -> Locale("mr", "IN")
            }
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

    /**
     * Distinct tactile Morse code SOS waveform (... --- ...) for high-urgency alerts
     */
    fun provideEmergencySosHaptic(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            // Morse code S-O-S: 3 short, 3 long, 3 short
            val timings = longArrayOf(
                0,
                120, 80, 120, 80, 120, 180,       // S (...)
                350, 100, 350, 100, 350, 180,     // O (---)
                120, 80, 120, 80, 120             // S (...)
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitudes = intArrayOf(
                    0,
                    VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0,
                    VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0,
                    VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (e: Exception) {
            // Ignore if vibration unavailable
        }
    }

    /**
     * Multi-lingual voice announcement when Emergency SOS or code red triage triggers
     */
    fun speakEmergencyAlert(context: Context, language: AppLanguage = AppLanguage.ENGLISH) {
        val alertText = when (language) {
            AppLanguage.HINDI -> "आपातकालीन एसओएस अलर्ट भेजा गया। 108 एम्बुलेंस और आशा कार्यकर्ता को सूचित कर दिया गया है।"
            AppLanguage.TAMIL -> "அவசர எஸ்ஓஎஸ் எச்சரிக்கை அனுப்பப்பட்டது. 108 ஆம்புலன்ஸ் மற்றும் ஆஷா பணியாளருக்கு தகவல் தெரிவிக்கப்பட்டது."
            AppLanguage.MARATHI -> "आपत्कालीन एसओएस अलर्ट पाठविला गेला आहे. 108 रुग्णवाहिका आणि आशा सेविकेला सूचित केले आहे."
            AppLanguage.ENGLISH -> "Emergency SOS broadcasted. 108 Ambulance and community health workers notified."
        }
        speak(context = context, text = alertText, language = language)
    }

    fun getSpokenHealthSummary(
        patientName: String,
        severity: SeverityLevel,
        heartRate: Int = 76,
        spO2: Int = 98,
        language: AppLanguage = AppLanguage.ENGLISH
    ): String {
        val firstName = patientName.split(" ").firstOrNull() ?: patientName
        return when (language) {
            AppLanguage.HINDI -> {
                when (severity) {
                    SeverityLevel.LOW ->
                        "नमस्ते $firstName जी। आज आपका स्वास्थ्य बिल्कुल ठीक है। दिल की धड़कन $heartRate और ऑक्सीजन $spO2 प्रतिशत सामान्य है।"
                    SeverityLevel.MODERATE ->
                        "नमस्ते $firstName जी। कृपया अपने स्वास्थ्य पर ध्यान दें। अपने आशा कार्यकर्ता या डॉक्टर से परामर्श लें।"
                    SeverityLevel.HIGH, SeverityLevel.SEVERE ->
                        "सावधान $firstName जी। तुरंत आपातकालीन सहायता लें या डॉक्टर से संपर्क करें।"
                }
            }
            AppLanguage.TAMIL -> {
                when (severity) {
                    SeverityLevel.LOW ->
                        "வணக்கம் $firstName. இன்று உங்கள் உடல்நலம் நன்றாக உள்ளது. இதயத் துடிப்பு $heartRate மற்றும் ஆக்சிஜன் அளவு $spO2 சதவீதம் இயல்பாக உள்ளது."
                    SeverityLevel.MODERATE ->
                        "வணக்கம் $firstName. உங்கள் உடல்நிலையில் கவனம் தேவை. உங்கள் ஆஷா பணியாளர் அல்லது மருத்துவரை அணுகவும்."
                    SeverityLevel.HIGH, SeverityLevel.SEVERE ->
                        "எச்சரிக்கை $firstName. உடனடி மருத்துவ உதவி பெறுங்கள் அல்லது அவசர சேவையைத் தொடர்பு கொள்ளுங்கள்."
                }
            }
            AppLanguage.MARATHI -> {
                when (severity) {
                    SeverityLevel.LOW ->
                        "नमस्ते $firstName जी. आज आपले आरोग्य पूर्णपणे ठीक आहे. हृदयाचे ठोके $heartRate आणि ऑक्सिजन $spO2 टक्के सामान्य आहे."
                    SeverityLevel.MODERATE ->
                        "नमस्ते $firstName जी. कृपया आपल्या आरोग्याकडे लक्ष द्या. आपल्या आशा सेविका किंवा डॉक्टरांचा सल्ला घ्या."
                    SeverityLevel.HIGH, SeverityLevel.SEVERE ->
                        "सावधान $firstName जी. त्वरित आपत्कालीन वैद्यकीय मदत घ्या किंवा डॉक्टरांशी संपर्क साधा."
                }
            }
            AppLanguage.ENGLISH -> {
                when (severity) {
                    SeverityLevel.LOW ->
                        "Hello $firstName. You are completely fine today. Your heart rate is $heartRate and oxygen is $spO2 percent, which is normal."
                    SeverityLevel.MODERATE ->
                        "Hello $firstName. Please pay attention to your health. Consult your ASHA worker or doctor."
                    SeverityLevel.HIGH, SeverityLevel.SEVERE ->
                        "Warning $firstName. Please seek immediate medical help or contact emergency services."
                }
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
