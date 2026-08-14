package com.vitalsense.app.core.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vitalsense.app.core.data.local.VitalSenseDatabase
import com.vitalsense.app.core.data.local.entity.*
import com.vitalsense.app.core.data.local.seed.SeedDataProvider
import com.vitalsense.app.core.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalSenseRepositoryImpl @Inject constructor(
    private val database: VitalSenseDatabase
) : VitalSenseRepository {

    private val gson = Gson()
    private val dao = database.vitalSenseDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    // In-memory reactive state caches for instant prototype responsiveness
    private val _villages = MutableStateFlow(SeedDataProvider.initialVillages)
    private val _patients = MutableStateFlow(SeedDataProvider.initialPatients)
    private val _ashaWorkers = MutableStateFlow(SeedDataProvider.initialAshaWorkers)
    private val _doctors = MutableStateFlow(SeedDataProvider.initialDoctors)
    private val _conditions = MutableStateFlow(SeedDataProvider.initialConditionRecords)
    private val _prescriptions = MutableStateFlow(SeedDataProvider.initialPrescriptions)
    private val _appointments = MutableStateFlow(SeedDataProvider.initialAppointments)
    private val _notices = MutableStateFlow(SeedDataProvider.initialNotices)
    private val _dispensary = MutableStateFlow(SeedDataProvider.initialDispensaryItems)
    private val _schemes = MutableStateFlow(SeedDataProvider.initialSchemes)

    init {
        // Seed Room DB asynchronously
        scope.launch {
            try {
                dao.insertVillages(SeedDataProvider.getVillageEntities())
                dao.insertAshaWorkers(SeedDataProvider.getAshaEntities())
                dao.insertDoctors(SeedDataProvider.getDoctorEntities())
                dao.insertPatients(SeedDataProvider.getPatientEntities())
                dao.insertConditionRecords(SeedDataProvider.getConditionEntities())
                dao.insertPrescriptions(SeedDataProvider.getPrescriptionEntities())
                dao.insertAppointments(SeedDataProvider.getAppointmentEntities())
                dao.insertDispensaryItems(SeedDataProvider.getDispensaryEntities())
                dao.insertNotices(SeedDataProvider.getNoticeEntities())
                dao.insertSchemes(SeedDataProvider.getSchemeEntities())
            } catch (e: Exception) {
                // Room seeding fallback to in-memory state
            }
        }
    }

    override fun getVillages(): Flow<List<Village>> = _villages.asStateFlow()

    override suspend fun addVillage(village: Village) {
        _villages.update { it + village }
        scope.launch {
            dao.insertVillages(listOf(
                VillageEntity(village.id, village.name, village.district, village.state, village.population, village.latitude, village.longitude, village.activeCases, village.highRiskCount)
            ))
        }
    }

    override fun getPatients(): Flow<List<Patient>> = _patients.asStateFlow()

    override fun getPatientById(id: String): Flow<Patient?> = _patients.map { list ->
        list.find { it.id == id }
    }

    override fun getPatientsForAsha(ashaId: String): Flow<List<Patient>> = _patients.map { list ->
        list.filter { it.ashaWorkerId == ashaId }
    }

    override suspend fun savePatient(patient: Patient) {
        _patients.update { list ->
            val index = list.indexOfFirst { it.id == patient.id }
            if (index >= 0) {
                list.toMutableList().apply { set(index, patient) }
            } else {
                list + patient
            }
        }
        scope.launch {
            dao.insertPatient(
                PatientEntity(
                    patient.id, patient.name, patient.age, patient.gender, patient.phone,
                    patient.villageId, patient.villageName, patient.ashaWorkerId,
                    patient.ashaWorkerName, patient.currentRiskLevel, patient.lastCondition,
                    patient.lastVisitDate, patient.nextAppointmentDate, patient.emergencyContact,
                    patient.profilePhotoUrl
                )
            )
        }
    }

    override fun getAshaWorkers(): Flow<List<AshaWorker>> = _ashaWorkers.asStateFlow()

    override fun getAshaWorkerById(id: String): Flow<AshaWorker?> = _ashaWorkers.map { list ->
        list.find { it.id == id || it.ashaUniqueId == id }
    }

    override fun getDoctors(): Flow<List<Doctor>> = _doctors.asStateFlow()

    override fun getDoctorById(id: String): Flow<Doctor?> = _doctors.map { list ->
        list.find { it.id == id }
    }

    override fun getConditionRecords(): Flow<List<ConditionRecord>> = _conditions.asStateFlow()

    override fun getConditionRecordsForPatient(patientId: String): Flow<List<ConditionRecord>> = _conditions.map { list ->
        list.filter { it.patientId == patientId }
    }

    override suspend fun logCondition(record: ConditionRecord) {
        _conditions.update { listOf(record) + it }
        // Update patient current risk level & last condition
        _patients.update { patients ->
            patients.map { p ->
                if (p.id == record.patientId) {
                    p.copy(
                        currentRiskLevel = record.severity,
                        lastCondition = record.notes.ifBlank { "${record.category.displayName} (${record.severity.displayName})" },
                        lastVisitDate = "Today"
                    )
                } else p
            }
        }
        // Update village active cases if severe/high
        _villages.update { villages ->
            villages.map { v ->
                if (v.id == record.villageId) {
                    v.copy(
                        activeCases = v.activeCases + 1,
                        highRiskCount = if (record.severity == SeverityLevel.HIGH || record.severity == SeverityLevel.SEVERE) v.highRiskCount + 1 else v.highRiskCount
                    )
                } else v
            }
        }
        scope.launch {
            dao.insertConditionRecord(
                ConditionRecordEntity(
                    record.id, record.patientId, record.patientName, record.villageId,
                    record.villageName, record.category, record.severity,
                    record.requestedDoctorType, record.notes, record.timestamp,
                    record.ashaProxyLogged, record.isPendingSync
                )
            )
        }
    }

    override fun getPrescriptions(): Flow<List<Prescription>> = _prescriptions.asStateFlow()

    override fun getPrescriptionsForPatient(patientId: String): Flow<List<Prescription>> = _prescriptions.map { list ->
        list.filter { it.patientId == patientId }
    }

    override suspend fun savePrescription(prescription: Prescription) {
        _prescriptions.update { listOf(prescription) + it }
        scope.launch {
            dao.insertPrescription(
                PrescriptionEntity(
                    prescription.id, prescription.patientId, prescription.patientName,
                    prescription.doctorId, prescription.doctorName, prescription.doctorSpecialty,
                    prescription.timestamp, prescription.dateFormatted,
                    gson.toJson(prescription.medicines), prescription.instructions,
                    prescription.isOcrExtracted
                )
            )
        }
    }

    override fun getAppointments(): Flow<List<Appointment>> = _appointments.asStateFlow()

    override fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>> = _appointments.map { list ->
        list.filter { it.patientId == patientId }
    }

    override fun getAppointmentsForDoctor(doctorId: String): Flow<List<Appointment>> = _appointments.map { list ->
        list.filter { it.doctorId == doctorId }
    }

    override suspend fun scheduleAppointment(appointment: Appointment) {
        _appointments.update { listOf(appointment) + it }
        // Update patient's next appointment date
        _patients.update { patients ->
            patients.map { p ->
                if (p.id == appointment.patientId) {
                    p.copy(nextAppointmentDate = "${appointment.dateFormatted} (${appointment.timeSlot})")
                } else p
            }
        }
        scope.launch {
            dao.insertAppointment(
                AppointmentEntity(
                    appointment.id, appointment.patientId, appointment.patientName,
                    appointment.doctorId, appointment.doctorName, appointment.doctorSpecialty,
                    appointment.dateFormatted, appointment.timeSlot, appointment.status,
                    appointment.proposedBy
                )
            )
        }
    }

    override fun getNotices(): Flow<List<BroadcastNotice>> = _notices.asStateFlow()

    override suspend fun sendNotice(notice: BroadcastNotice) {
        _notices.update { listOf(notice) + it }
        scope.launch {
            dao.insertNotice(
                BroadcastNoticeEntity(
                    notice.id, notice.senderRole, notice.senderName, notice.targetRole,
                    notice.targetVillage, notice.title, notice.message, notice.timestamp,
                    notice.isUrgent
                )
            )
        }
    }

    override fun getDispensaryStock(): Flow<List<DispensaryItem>> = _dispensary.asStateFlow()

    override fun getGovernmentSchemes(): Flow<List<GovernmentScheme>> = _schemes.asStateFlow()

    override suspend fun triggerEmergencySos(
        patient: Patient,
        locationLat: Double?,
        locationLng: Double?
    ): Boolean {
        // Create high-priority notice to ASHA & district admin
        val sosNotice = BroadcastNotice(
            id = "sos_${System.currentTimeMillis()}",
            senderRole = UserRole.PATIENT,
            senderName = "${patient.name} (SOS ALERT)",
            targetRole = "ASHA",
            targetVillage = patient.villageName,
            title = "🚨 EMERGENCY SOS: ${patient.name}",
            message = "Patient ${patient.name} (${patient.villageName}, Age ${patient.age}) triggered an Emergency SOS! Contact: ${patient.phone}. Location: Lat ${locationLat ?: 26.8467}, Lng ${locationLng ?: 80.9462}.",
            timestamp = System.currentTimeMillis(),
            isUrgent = true
        )
        sendNotice(sosNotice)
        return true
    }
}
