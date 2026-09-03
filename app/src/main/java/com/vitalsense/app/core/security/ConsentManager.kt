package com.vitalsense.app.core.security

import com.vitalsense.app.core.data.model.AuditLog
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsentManager @Inject constructor(
    private val repository: VitalSenseRepository
) {

    /**
     * Records a consent interaction where a patient grants proxy access to an ASHA worker.
     */
    suspend fun recordAshaProxyConsent(
        patientId: String,
        ashaId: String,
        details: String = "Patient granted proxy access to ASHA for teleconsultation"
    ) {
        val auditLog = AuditLog(
            actorId = ashaId,
            actorRole = "ASHA",
            action = "CONSENT_GRANTED",
            resourceId = patientId,
            resourceType = "PATIENT_PROFILE",
            details = details
        )
        repository.logAuditAction(auditLog)
    }

    /**
     * Records a medical record access action.
     */
    suspend fun recordMedicalRecordAccess(
        actorId: String,
        actorRole: String,
        patientId: String,
        recordId: String,
        recordType: String
    ) {
        val auditLog = AuditLog(
            actorId = actorId,
            actorRole = actorRole,
            action = "RECORD_ACCESSED",
            resourceId = recordId,
            resourceType = recordType,
            details = "Accessed $recordType for patient $patientId"
        )
        repository.logAuditAction(auditLog)
    }

    /**
     * Records an emergency override where access is granted without explicit consent due to an emergency.
     */
    suspend fun recordEmergencyOverride(
        actorId: String,
        actorRole: String,
        patientId: String,
        reason: String
    ) {
        val auditLog = AuditLog(
            actorId = actorId,
            actorRole = actorRole,
            action = "EMERGENCY_OVERRIDE",
            resourceId = patientId,
            resourceType = "PATIENT_PROFILE",
            details = reason
        )
        repository.logAuditAction(auditLog)
    }
}
