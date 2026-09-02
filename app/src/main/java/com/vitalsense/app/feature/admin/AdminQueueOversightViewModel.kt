package com.vitalsense.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalsense.app.core.data.model.DoctorQueueSummary
import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AdminQueueOversightViewModel @Inject constructor(
    private val repository: VitalSenseRepository
) : ViewModel() {

    val todayFormatted: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val allDoctorSummaries: StateFlow<List<DoctorQueueSummary>> = repository.observeAllDoctorQueueSummaries(todayFormatted)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDoctorId = MutableStateFlow<String?>(null)
    val selectedDoctorId: StateFlow<String?> = _selectedDoctorId.asStateFlow()

    val selectedDoctorQueue: StateFlow<List<QueueEntry>> = _selectedDoctorId.flatMapLatest { doctorId ->
        if (doctorId != null) repository.observeDoctorQueue(doctorId, todayFormatted)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDoctor(doctorId: String) {
        _selectedDoctorId.value = doctorId
    }

    fun clearSelectedDoctor() {
        _selectedDoctorId.value = null
    }
}
