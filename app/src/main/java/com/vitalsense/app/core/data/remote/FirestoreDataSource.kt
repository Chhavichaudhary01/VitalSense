package com.vitalsense.app.core.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vitalsense.app.core.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "VitalSenseFirebase"

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Collection references
    private val patientsCollection = firestore.collection("patients")
    private val conditionsCollection = firestore.collection("condition_records")
    private val prescriptionsCollection = firestore.collection("prescriptions")
    private val appointmentsCollection = firestore.collection("appointments")
    private val noticesCollection = firestore.collection("broadcast_notices")
    private val villagesCollection = firestore.collection("villages")

    init {
        // Ensure an authenticated session for Firestore security rules
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().addOnSuccessListener {
                    Log.d(TAG, "FirebaseAuth: Anonymous sign-in success. UID=${it.user?.uid}")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "FirebaseAuth: Anonymous sign-in failed (Rules in test mode will still work): ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth init error: ${e.message}")
        }
    }

    // --- PUSH OPERATIONS (Writes) ---

    suspend fun uploadConditionRecord(record: ConditionRecord) {
        try {
            val data = hashMapOf(
                "id" to record.id,
                "patientId" to record.patientId,
                "patientName" to record.patientName,
                "villageId" to record.villageId,
                "villageName" to record.villageName,
                "category" to record.category.name,
                "severity" to record.severity.name,
                "requestedDoctorType" to record.requestedDoctorType.name,
                "notes" to record.notes,
                "timestamp" to record.timestamp,
                "ashaProxyLogged" to record.ashaProxyLogged,
                "isPendingSync" to false
            )
            conditionsCollection.document(record.id).set(data).await()
            Log.d(TAG, "✅ Successfully uploaded condition_record: ${record.id} (${record.patientName})")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to upload condition_record: ${e.message}", e)
            throw e
        }
    }

    suspend fun uploadPrescription(prescription: Prescription) {
        try {
            val data = hashMapOf(
                "id" to prescription.id,
                "patientId" to prescription.patientId,
                "patientName" to prescription.patientName,
                "doctorId" to prescription.doctorId,
                "doctorName" to prescription.doctorName,
                "doctorSpecialty" to prescription.doctorSpecialty,
                "timestamp" to prescription.timestamp,
                "dateFormatted" to prescription.dateFormatted,
                "medicines" to prescription.medicines.map { med ->
                    hashMapOf(
                        "name" to med.name,
                        "dosage" to med.dosage,
                        "frequency" to med.frequency,
                        "duration" to med.duration,
                        "quantity" to med.quantity
                    )
                },
                "instructions" to prescription.instructions,
                "isOcrExtracted" to prescription.isOcrExtracted
            )
            prescriptionsCollection.document(prescription.id).set(data).await()
            Log.d(TAG, "✅ Successfully uploaded prescription: ${prescription.id}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to upload prescription: ${e.message}", e)
            throw e
        }
    }

    suspend fun uploadAppointment(appointment: Appointment) {
        try {
            val data = hashMapOf(
                "id" to appointment.id,
                "patientId" to appointment.patientId,
                "patientName" to appointment.patientName,
                "doctorId" to appointment.doctorId,
                "doctorName" to appointment.doctorName,
                "doctorSpecialty" to appointment.doctorSpecialty,
                "dateFormatted" to appointment.dateFormatted,
                "timeSlot" to appointment.timeSlot,
                "status" to appointment.status,
                "proposedBy" to appointment.proposedBy.name
            )
            appointmentsCollection.document(appointment.id).set(data).await()
            Log.d(TAG, "✅ Successfully uploaded appointment: ${appointment.id}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to upload appointment: ${e.message}", e)
            throw e
        }
    }

    suspend fun uploadPatient(patient: Patient) {
        try {
            val data = hashMapOf(
                "id" to patient.id,
                "name" to patient.name,
                "age" to patient.age,
                "gender" to patient.gender,
                "phone" to patient.phone,
                "villageId" to patient.villageId,
                "villageName" to patient.villageName,
                "ashaWorkerId" to patient.ashaWorkerId,
                "ashaWorkerName" to patient.ashaWorkerName,
                "currentRiskLevel" to patient.currentRiskLevel.name,
                "lastCondition" to patient.lastCondition,
                "lastVisitDate" to patient.lastVisitDate,
                "nextAppointmentDate" to patient.nextAppointmentDate,
                "emergencyContact" to patient.emergencyContact,
                "profilePhotoUrl" to patient.profilePhotoUrl
            )
            patientsCollection.document(patient.id).set(data).await()
            Log.d(TAG, "✅ Successfully uploaded patient: ${patient.id} (${patient.name})")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to upload patient: ${e.message}", e)
            throw e
        }
    }

    suspend fun uploadNotice(notice: BroadcastNotice) {
        try {
            val data = hashMapOf(
                "id" to notice.id,
                "senderRole" to notice.senderRole.name,
                "senderName" to notice.senderName,
                "targetRole" to notice.targetRole,
                "targetVillage" to notice.targetVillage,
                "title" to notice.title,
                "message" to notice.message,
                "timestamp" to notice.timestamp,
                "isUrgent" to notice.isUrgent
            )
            noticesCollection.document(notice.id).set(data).await()
            Log.d(TAG, "✅ Successfully uploaded broadcast_notice: ${notice.id} - ${notice.title}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to upload broadcast_notice: ${e.message}", e)
            throw e
        }
    }

    // --- REAL-TIME LISTENERS (Reads) ---

    fun getConditionRecordsStream(): Flow<List<ConditionRecord>> = callbackFlow {
        val listener = conditionsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Condition records stream error: ${error.message}")
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        ConditionRecord(
                            id = doc.getString("id") ?: doc.id,
                            patientId = doc.getString("patientId") ?: "",
                            patientName = doc.getString("patientName") ?: "",
                            villageId = doc.getString("villageId") ?: "",
                            villageName = doc.getString("villageName") ?: "",
                            category = ConditionCategory.valueOf(doc.getString("category") ?: ConditionCategory.GENERAL_MEDICINE.name),
                            severity = SeverityLevel.valueOf(doc.getString("severity") ?: SeverityLevel.LOW.name),
                            requestedDoctorType = DoctorSpecialty.valueOf(doc.getString("requestedDoctorType") ?: DoctorSpecialty.GENERAL_PHYSICIAN.name),
                            notes = doc.getString("notes") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            ashaProxyLogged = doc.getBoolean("ashaProxyLogged") ?: false,
                            isPendingSync = false
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getBroadcastNoticesStream(): Flow<List<BroadcastNotice>> = callbackFlow {
        val listener = noticesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Broadcast notices stream error: ${error.message}")
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        BroadcastNotice(
                            id = doc.getString("id") ?: doc.id,
                            senderRole = UserRole.valueOf(doc.getString("senderRole") ?: UserRole.ADMIN.name),
                            senderName = doc.getString("senderName") ?: "",
                            targetRole = doc.getString("targetRole") ?: "ALL",
                            targetVillage = doc.getString("targetVillage") ?: "All Villages",
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            isUrgent = doc.getBoolean("isUrgent") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }
}