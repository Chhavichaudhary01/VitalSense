package com.vitalsense.app.core.data.model

import java.util.UUID

data class AuditLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val actorId: String,       // e.g. Asha ID, Patient ID, Doctor ID
    val actorRole: String,     // "ASHA", "PATIENT", "DOCTOR", "ADMIN"
    val action: String,        // e.g. "CONSENT_GRANTED", "RECORD_ACCESSED", "EMERGENCY_TRIGGERED"
    val resourceId: String? = null,   // e.g. Patient ID who granted consent
    val resourceType: String? = null, // e.g. "PATIENT_PROFILE", "MEDICAL_RECORD"
    val details: String? = null,      // JSON or text details of the action
    val isSynced: Boolean = false
)
