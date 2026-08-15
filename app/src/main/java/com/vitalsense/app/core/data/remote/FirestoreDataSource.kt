package com.vitalsense.app.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.vitalsense.app.core.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

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

    // --- PUSH OPERATIONS (Writes) ---

    suspend fun uploadConditionRecord(record: ConditionRecord) {
        conditionsCollection.document(record.id).set(record).await()
    }

    suspend fun uploadPrescription(prescription: Prescription) {
        prescriptionsCollection.document(prescription.id).set(prescription).await()
    }

    suspend fun uploadAppointment(appointment: Appointment) {
        appointmentsCollection.document(appointment.id).set(appointment).await()
    }

    suspend fun uploadPatient(patient: Patient) {
        patientsCollection.document(patient.id).set(patient).await()
    }

    suspend fun uploadNotice(notice: BroadcastNotice) {
        noticesCollection.document(notice.id).set(notice).await()
    }

    // --- REAL-TIME LISTENERS (Reads) ---

    fun getConditionRecordsStream(): Flow<List<ConditionRecord>> = callbackFlow {
        val listener = conditionsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val records = snapshot.toObjects(ConditionRecord::class.java)
                trySend(records)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getBroadcastNoticesStream(): Flow<List<BroadcastNotice>> = callbackFlow {
        val listener = noticesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val notices = snapshot.toObjects(BroadcastNotice::class.java)
                trySend(notices)
            }
        }
        awaitClose { listener.remove() }
    }
}