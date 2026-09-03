package com.vitalsense.app.core.util

import com.vitalsense.app.core.data.model.ConditionCategory
import com.vitalsense.app.core.data.model.SeverityLevel

object TriageEngine {
    
    /**
     * Determines the severity level of a patient's condition based on reported symptoms and vitals.
     * This simulates an intelligent triage logic for the SIH 26133 prototype.
     */
    fun evaluateSeverity(
        category: ConditionCategory,
        symptoms: List<String>,
        systolicBp: Int? = null,
        diastolicBp: Int? = null,
        spo2: Int? = null,
        temperatureF: Float? = null,
        pulseRate: Int? = null
    ): SeverityLevel {
        
        // 1. Explicit red-flag symptoms bypass everything to EMERGENCY
        val redFlagSymptoms = listOf(
            "chest pain", "shortness of breath", "unconscious", 
            "heavy bleeding", "seizure", "paralysis", "sudden weakness"
        )
        
        if (symptoms.any { symptom -> redFlagSymptoms.any { symptom.contains(it, ignoreCase = true) } }) {
            return SeverityLevel.SEVERE
        }

        // 2. Critical Vitals -> SEVERE (Emergency)
        if (spo2 != null && spo2 < 90) return SeverityLevel.SEVERE
        if (systolicBp != null && systolicBp > 180) return SeverityLevel.SEVERE
        if (diastolicBp != null && diastolicBp > 120) return SeverityLevel.SEVERE
        if (pulseRate != null && (pulseRate > 130 || pulseRate < 40)) return SeverityLevel.SEVERE
        if (temperatureF != null && temperatureF > 104.0f) return SeverityLevel.SEVERE

        // 3. High Risk Vitals -> HIGH
        if (spo2 != null && spo2 in 90..94) return SeverityLevel.HIGH
        if (systolicBp != null && systolicBp in 160..179) return SeverityLevel.HIGH
        if (diastolicBp != null && diastolicBp in 100..119) return SeverityLevel.HIGH
        if (temperatureF != null && temperatureF in 102.0f..104.0f) return SeverityLevel.HIGH
        
        // 4. Moderate Risk Vitals -> MODERATE
        if (systolicBp != null && systolicBp in 140..159) return SeverityLevel.MODERATE
        if (diastolicBp != null && diastolicBp in 90..99) return SeverityLevel.MODERATE
        if (temperatureF != null && temperatureF in 100.4f..101.9f) return SeverityLevel.MODERATE

        // 5. Category-based fallback
        return when (category) {
            ConditionCategory.EMERGENCY -> SeverityLevel.HIGH
            ConditionCategory.MATERNAL_HEALTH -> SeverityLevel.MODERATE
            ConditionCategory.MENTAL_HEALTH -> SeverityLevel.MODERATE
            else -> SeverityLevel.LOW
        }
    }
}
