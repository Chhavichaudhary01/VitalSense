package com.vitalsense.app.core.data.model

data class Village(
    val id: String,
    val name: String,
    val district: String,
    val state: String,
    val population: Int,
    val latitude: Double,
    val longitude: Double,
    val activeCases: Int,
    val highRiskCount: Int
)

data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val villageId: String,
    val villageName: String,
    val ashaWorkerId: String,
    val ashaWorkerName: String,
    val currentRiskLevel: SeverityLevel,
    val lastCondition: String,
    val lastVisitDate: String,
    val nextAppointmentDate: String?,
    val emergencyContact: String,
    val profilePhotoUrl: String? = null
)

data class AshaWorker(
    val id: String,
    val name: String,
    val ashaUniqueId: String,
    val phone: String,
    val assignedVillages: List<String>,
    val activePatientCount: Int,
    val alertCount: Int
)

enum class CallType {
    VIDEO,
    VOICE
}

enum class DoctorAvailabilityStatus {
    AVAILABLE,
    BUSY,
    OFFLINE
}

enum class EmergencyCallOutcome {
    CONNECTED,
    ESCALATED_NEXT_DOCTOR,
    FELL_BACK_TO_SMS
}

data class CallLog(
    val id: String,
    val callType: CallType,
    val callMode: String, // "APPOINTMENT" or "EMERGENCY"
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val outcome: EmergencyCallOutcome,
    val outcomeNotes: String? = null
)

data class Doctor(
    val id: String,
    val name: String,
    val specialty: DoctorSpecialty,
    val qualification: String,
    val hospitalName: String,
    val distanceKm: Double,
    val phone: String,
    val availableDays: String,
    val onCallStatus: DoctorAvailabilityStatus = DoctorAvailabilityStatus.AVAILABLE
)

data class ConditionRecord(
    val id: String,
    val patientId: String,
    val patientName: String,
    val villageId: String,
    val villageName: String,
    val category: ConditionCategory,
    val severity: SeverityLevel,
    val requestedDoctorType: DoctorSpecialty,
    val notes: String,
    val timestamp: Long,
    val ashaProxyLogged: Boolean = false,
    val status: CaseStatus = CaseStatus.PENDING_REVIEW,
    val assignedDoctorId: String? = null,
    val assignedDoctorName: String? = null,
    val doctorResponse: String? = null,
    val doctorResponseTimestamp: Long? = null,
    val doctorResponseDoctorName: String? = null,
    val privateDoctorNotes: String? = null,
    val referredByDoctorId: String? = null,
    val referredByDoctorName: String? = null,
    val referralNotes: String? = null,
    val isPendingSync: Boolean = false
)

data class PrescribedMedicine(
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val quantity: Int,
    val medicineId: String? = null,
    val hasAlternativeAvailable: Boolean = false
)

data class Prescription(
    val id: String,
    val caseId: String? = null,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialty: String,
    val timestamp: Long,
    val dateFormatted: String,
    val medicines: List<PrescribedMedicine>,
    val instructions: String,
    val isOcrExtracted: Boolean = false
)

data class Appointment(
    val id: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialty: String,
    val dateFormatted: String,
    val timeSlot: String,
    val status: String, // "Confirmed", "Pending", "Declined", "Completed", "Missed"
    val proposedBy: UserRole,
    val outcomeNotes: String? = null,
    val callType: CallType = CallType.VIDEO,
    val scheduledTimestamp: Long = 0L
)

data class BroadcastNotice(
    val id: String,
    val senderRole: UserRole,
    val senderName: String,
    val targetRole: String,
    val targetVillage: String?,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isUrgent: Boolean = false
)

data class DispensaryItem(
    val id: String,
    val medicineName: String,
    val category: String,
    val availableQuantity: Int,
    val unit: String,
    val reorderThreshold: Int,
    val lastRestockDateFormatted: String? = null
) {
    val isLowStock: Boolean
        get() = availableQuantity <= reorderThreshold
}

data class GovernmentScheme(
    val id: String,
    val title: String,
    val category: String,
    val targetBeneficiary: String,
    val benefitsSummary: String,
    val eligibility: String,
    val applicationUrl: String = ""
)

data class VaccineInfo(
    val name: String,
    val dueDateFormatted: String,
    val givenDateFormatted: String?,
    val status: String // "Upcoming", "Completed", "Overdue"
)

data class ImmunizationRecord(
    val id: String,
    val childName: String,
    val motherName: String,
    val dobFormatted: String,
    val gender: String,
    val villageName: String,
    val ashaWorkerId: String,
    val vaccines: List<VaccineInfo>
)

data class DailyRound(
    val id: String,
    val dateFormatted: String,
    val villageName: String,
    val householdName: String,
    val personName: String,
    val ashaWorkerId: String,
    val purpose: String,
    val isPregnancyChecked: Boolean,
    val isChildHealthChecked: Boolean,
    val isImmunizationChecked: Boolean,
    val isMedicineGiven: Boolean,
    val isCounsellingDone: Boolean,
    val notes: String,
    val status: String // "Pending", "Completed"
)

data class AshaMedicine(
    val id: String,
    val ashaWorkerId: String,
    val medicineName: String,
    val availableQuantity: Int,
    val unit: String,
    val minStockQuantity: Int,
    val expiryDateFormatted: String,
    val lastRestockDateFormatted: String?
) {
    val isLowStock: Boolean
        get() = availableQuantity <= minStockQuantity
}

data class DiseaseTrendRecord(
    val id: String,
    val villageName: String,
    val diseaseName: String,
    val caseCount: Int,
    val dateFormatted: String,
    val severity: String?
)

data class LabTestItem(
    val testName: String,
    val resultValue: String,
    val unit: String,
    val referenceRange: String,
    val flag: String // "NORMAL", "HIGH", "LOW"
)

data class LabReport(
    val id: String,
    val patientId: String,
    val patientName: String,
    val testCategory: String, // "Complete Blood Count (CBC)", "Biochemistry / Diabetes", "Liver Function Test (LFT)", "Dengue & Serology", "Urinalysis"
    val doctorName: String,
    val dateFormatted: String,
    val items: List<LabTestItem>,
    val notes: String = "Clinically verified by Pathology Dept.",
    val status: String = "Verified" // "Pending", "Verified"
)

data class OpdToken(
    val id: String,
    val tokenNumber: String, // e.g. "OPD-A24"
    val patientId: String,
    val patientName: String,
    val doctorName: String,
    val department: String, // "General Medicine", "Orthopedics & Trauma", "Maternal Health", "Pediatrics"
    val cabinNumber: String, // e.g. "Room 4"
    val currentServingToken: String, // e.g. "OPD-A21"
    val estimatedWaitMinutes: Int,
    val status: String, // "In Queue", "Serving", "Completed"
    val dateFormatted: String
)

data class MedicalCertificate(
    val id: String,
    val certificateNumber: String, // e.g. "MC-2026-9812"
    val patientId: String,
    val patientName: String,
    val patientAge: Int,
    val patientGender: String,
    val doctorName: String,
    val doctorRegistrationNumber: String, // e.g. "MCI-489201"
    val diagnosis: String,
    val restStartDate: String,
    val restEndDate: String,
    val fitDate: String,
    val certificateType: String, // "Sick Leave Certificate", "Medical Fitness Certificate"
    val issuedDateFormatted: String
)

data class FamilyMember(
    val id: String,
    val primaryPatientId: String,
    val name: String,
    val relationship: String, // "Spouse", "Child", "Parent"
    val age: Int,
    val gender: String,
    val bloodGroup: String,
    val abhaId: String
)

data class BloodStockItem(
    val id: String,
    val bloodGroup: String, // "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    val unitsAvailable: Int,
    val hospitalName: String,
    val contactPhone: String,
    val status: String = "Available" // "Available", "Low Stock", "Critical"
) {
    val isCritical: Boolean
        get() = unitsAvailable <= 5
}

data class IpdBed(
    val id: String,
    val wardName: String, // "Male Medical Ward", "Female & Maternal Ward", "Emergency Trauma Ward", "Intensive Care Unit (ICU)"
    val bedNumber: String, // "BED-01", "ICU-03"
    val isOccupied: Boolean,
    val patientId: String? = null,
    val patientName: String? = null,
    val admissionDate: String? = null,
    val attendingDoctorName: String? = null,
    val diagnosis: String? = null,
    val nurseInCharge: String? = null
)

data class OtSurgeryBooking(
    val id: String,
    val otRoomName: String, // "Major OT-1", "Trauma & Ortho OT-2", "Emergency Minor OT"
    val patientId: String,
    val patientName: String,
    val surgeryName: String, // "Open Reduction & Internal Fixation", "Maxillofacial Reconstruction", "Elective Appendectomy"
    val surgeonName: String, // "Dr. Ayushman Dev Singh"
    val anesthetistName: String,
    val scheduledDate: String,
    val scheduledTimeSlot: String, // "09:00 AM - 11:30 AM"
    val pacCleared: Boolean = true, // Pre-Anesthesia Checkup cleared
    val status: String = "Scheduled" // "Scheduled", "In-Progress", "Completed", "Post-Op Recovery"
)

data class ExternalReferral(
    val id: String,
    val referralLetterId: String, // "REF-2026-4401"
    val patientId: String,
    val patientName: String,
    val referringDoctorName: String,
    val empanelledHospitalName: String, // "AIIMS New Delhi", "Railway Central Hospital, New Delhi", "KGMU Lucknow"
    val specialtyRequired: String, // "Cardiothoracic Surgery", "Neurosurgery & Spine", "Medical Oncology"
    val clinicalSummary: String,
    val isCashlessApproved: Boolean = true,
    val ambulanceRequisitioned: Boolean = false,
    val issuedDate: String,
    val status: String = "Active" // "Active", "Reported", "Closed"
)

data class BioMedicalEquipment(
    val id: String,
    val assetCode: String, // "BME-OX-104", "BME-ECG-02"
    val name: String, // "PSA Oxygen Generation Plant 250 LPM", "12-Lead Digital ECG Machine", "Biphasic Defibrillator"
    val department: String, // "Critical Care / ICU", "Emergency Trauma", "Radiology & Diagnostics"
    val status: String, // "OPERATIONAL", "CALIBRATION_DUE", "UNDER_MAINTENANCE"
    val lastServiceDate: String,
    val nextServiceDue: String,
    val location: String,
    val inChargeContact: String
)

// --- Live Queue & Appointment Domain Models ---

enum class QueueEntrySource { SCHEDULED, WALK_IN }

enum class QueueEntryStatus {
    WAITING, CALLED, IN_CONSULTATION, COMPLETED, NO_SHOW, SKIPPED, CANCELLED
}

data class DoctorDaySlotConfig(
    val id: String,
    val doctorId: String,
    val dateFormatted: String,       // "yyyy-MM-dd"
    val startTime: String,           // "HH:mm"
    val endTime: String,             // "HH:mm"
    val capacity: Int,               // max scheduled bookings in this block
    val isWalkInOpen: Boolean = true // whether walk-ins can join today's queue
)

data class QueueEntry(
    val id: String,
    val doctorId: String,
    val doctorName: String,
    val dateFormatted: String,
    val tokenNumber: Int,
    val provisionalToken: Boolean = false, // true until an offline check-in is reconciled with the server
    val appointmentId: String? = null,            // null for walk-ins
    val patientId: String,
    val patientName: String,
    val source: QueueEntrySource,
    val status: QueueEntryStatus,
    val priorityFlag: Boolean = false,     // doctor-set manual priority bump
    val checkedInAt: Long,
    val calledAt: Long? = null,
    val consultationStartedAt: Long? = null,
    val completedAt: Long? = null,
    val outcomeNotes: String? = null,
    val isPendingSync: Boolean = false
)

data class DoctorQueueSummary(
    val doctorId: String,
    val doctorName: String,
    val dateFormatted: String,
    val waitingCount: Int,
    val currentToken: Int,
    val avgWaitSeconds: Long,
    val isQueueOpen: Boolean
)

