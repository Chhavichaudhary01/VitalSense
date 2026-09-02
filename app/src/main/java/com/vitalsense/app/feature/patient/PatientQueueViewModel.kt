package com.vitalsense.app.feature.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import com.vitalsense.app.core.state.AppStateHolder
import com.vitalsense.app.core.util.QueueEtaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PatientQueueViewModel @Inject constructor(
    private val repository: VitalSenseRepository,
    private val appStateHolder: AppStateHolder
) : ViewModel() {

    val activePatient: StateFlow<Patient> = appStateHolder.activePatient
    val effectivePatient: Flow<Patient> = combine(
        appStateHolder.activePatient,
        appStateHolder.activeProxyPatient
    ) { direct, proxy -> proxy ?: direct }

    val todayFormatted: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val queueEntry: StateFlow<QueueEntry?> = effectivePatient.flatMapLatest { patient ->
        repository.observePatientQueueEntry(patient.id, todayFormatted)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Doctor queue stream for the doctor of current patient's entry
    val doctorQueue: StateFlow<List<QueueEntry>> = queueEntry.flatMapLatest { entry ->
        if (entry != null) repository.observeDoctorQueue(entry.doctorId, todayFormatted)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val position: StateFlow<Int> = combine(queueEntry, doctorQueue) { entry, queue ->
        if (entry != null && entry.status == QueueEntryStatus.WAITING) {
            val waiting = QueueEtaCalculator.sortWaitingEntries(queue)
            QueueEtaCalculator.calculatePosition(entry.id, waiting)
        } else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val estimatedWaitMinutes: StateFlow<Long> = combine(position, doctorQueue) { pos, queue ->
        val avgSec = QueueEtaCalculator.averageConsultationSeconds(queue)
        (QueueEtaCalculator.calculateWaitTimeSeconds(pos, avgSec) + 59) / 60
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10L)

    fun checkIn(appointmentId: String) {
        viewModelScope.launch {
            repository.checkInAppointment(appointmentId)
        }
    }

    fun joinWalkIn(doctorId: String, doctorName: String) {
        val patient = appStateHolder.activeProxyPatient.value ?: appStateHolder.activePatient.value
        viewModelScope.launch {
            repository.joinWalkInQueue(doctorId, doctorName, patient.id, patient.name)
        }
    }

    fun cancelQueueEntry(entryId: String) {
        viewModelScope.launch {
            repository.cancelQueueEntry(entryId)
        }
    }
}
