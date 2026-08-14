package com.vitalsense.app.core.data.local.dao

import androidx.room.*
import com.vitalsense.app.core.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalSenseDao {

    // --- Villages ---
    @Query("SELECT * FROM villages ORDER BY activeCases DESC")
    fun getAllVillages(): Flow<List<VillageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVillages(villages: List<VillageEntity>)

    // --- Patients ---
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientById(id: String): Flow<PatientEntity?>

    @Query("SELECT * FROM patients WHERE ashaWorkerId = :ashaId")
    fun getPatientsByAsha(ashaId: String): Flow<List<PatientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    // --- ASHA Workers ---
    @Query("SELECT * FROM asha_workers")
    fun getAllAshaWorkers(): Flow<List<AshaWorkerEntity>>

    @Query("SELECT * FROM asha_workers WHERE id = :id OR ashaUniqueId = :uniqueId LIMIT 1")
    fun getAshaWorker(id: String, uniqueId: String): Flow<AshaWorkerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAshaWorkers(ashaWorkers: List<AshaWorkerEntity>)

    // --- Doctors ---
    @Query("SELECT * FROM doctors")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE id = :id")
    fun getDoctorById(id: String): Flow<DoctorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(doctors: List<DoctorEntity>)

    // --- Condition Records ---
    @Query("SELECT * FROM condition_records ORDER BY timestamp DESC")
    fun getAllConditionRecords(): Flow<List<ConditionRecordEntity>>

    @Query("SELECT * FROM condition_records WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getConditionsForPatient(patientId: String): Flow<List<ConditionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConditionRecord(record: ConditionRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConditionRecords(records: List<ConditionRecordEntity>)

    // --- Prescriptions ---
    @Query("SELECT * FROM prescriptions ORDER BY timestamp DESC")
    fun getAllPrescriptions(): Flow<List<PrescriptionEntity>>

    @Query("SELECT * FROM prescriptions WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getPrescriptionsForPatient(patientId: String): Flow<List<PrescriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: PrescriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptions(prescriptions: List<PrescriptionEntity>)

    // --- Appointments ---
    @Query("SELECT * FROM appointments ORDER BY dateFormatted ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId")
    fun getAppointmentsForPatient(patientId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId")
    fun getAppointmentsForDoctor(doctorId: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    // --- Broadcast Notices ---
    @Query("SELECT * FROM broadcast_notices ORDER BY timestamp DESC")
    fun getAllNotices(): Flow<List<BroadcastNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: BroadcastNoticeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<BroadcastNoticeEntity>)

    // --- Dispensary Stock ---
    @Query("SELECT * FROM dispensary_stock ORDER BY medicineName ASC")
    fun getAllDispensaryItems(): Flow<List<DispensaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispensaryItems(items: List<DispensaryEntity>)

    // --- Government Schemes ---
    @Query("SELECT * FROM government_schemes")
    fun getAllSchemes(): Flow<List<GovernmentSchemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchemes(schemes: List<GovernmentSchemeEntity>)
}
