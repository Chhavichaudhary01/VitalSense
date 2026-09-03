package com.vitalsense.app.core.call

import android.content.Context
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.util.EmergencySosHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

enum class CallMode {
    APPOINTMENT,
    EMERGENCY
}

enum class CallSessionState {
    IDLE,
    OUTGOING_RINGING,
    INCOMING_RINGING,
    CONNECTING,
    CONNECTED,
    POOR_CONNECTION_SUGGEST_VOICE,
    ENDED,
    FALLBACK_TO_CELLULAR
}

data class ActiveCallSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val mode: CallMode,
    val type: CallType,
    val patientId: String,
    val patientName: String,
    val patientAge: Int = 34,
    val villageName: String = "Sundarpura",
    val patientVitalsSummary: String = "HR: 76 bpm · BP: 120/80 · SpO2: 98% · Temp: 98.6°F",
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialty: String = "General Physician",
    val doctorPhone: String = "9876543210",
    val state: CallSessionState = CallSessionState.CONNECTING,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isLowBandwidthMode: Boolean = false,
    val jitsiRoomUrl: String = "",
    val escalationCount: Int = 0,
    val statusMessage: String = "Connecting…",
    val outcome: EmergencyCallOutcome = EmergencyCallOutcome.CONNECTED,
    val outcomeNotes: String? = null
)

object TeleCallingManager {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var callTimerJob: Job? = null
    private var emergencyEscalationJob: Job? = null

    private val _currentSession = MutableStateFlow<ActiveCallSession?>(null)
    val currentSession: StateFlow<ActiveCallSession?> = _currentSession.asStateFlow()

    // Listener for persisting completed calls to Room outbox/repo
    var onCallCompletedListener: ((CallLog) -> Unit)? = null

    /**
     * Initiates a scheduled appointment call (both sides expect it, calm tone).
     */
    fun startAppointmentCall(
        appointment: Appointment,
        isDoctor: Boolean
    ): ActiveCallSession {
        resetActiveJobs()

        val jitsiUrl = AppointmentScheduleHelper.generateJitsiRoomUrl(appointment.id)
        val initialType = appointment.callType

        val session = ActiveCallSession(
            mode = CallMode.APPOINTMENT,
            type = initialType,
            patientId = appointment.patientId,
            patientName = appointment.patientName,
            doctorId = appointment.doctorId,
            doctorName = appointment.doctorName,
            doctorSpecialty = appointment.doctorSpecialty,
            state = if (isDoctor) CallSessionState.CONNECTED else CallSessionState.OUTGOING_RINGING,
            statusMessage = if (isDoctor) "Connected with patient" else "Waiting for doctor to join…",
            isLowBandwidthMode = initialType == CallType.VOICE,
            isCameraOff = initialType == CallType.VOICE,
            jitsiRoomUrl = jitsiUrl
        )

        _currentSession.value = session
        startDurationTimer()

        // If patient initiated, simulate doctor joining within 3 seconds
        if (!isDoctor) {
            scope.launch {
                delay(3000)
                _currentSession.update { current ->
                    current?.copy(
                        state = CallSessionState.CONNECTED,
                        statusMessage = "Connected with Dr. ${current.doctorName}"
                    )
                }
            }
        }

        return session
    }

    /**
     * Initiates an urgent emergency call.
     * Tries the assigned doctor first with a 25-second timeout, then escalates
     * through the on-call queue, and finally falls back to SMS+GPS SOS.
     */
    fun startEmergencyCall(
        context: Context? = null,
        patient: Patient,
        callType: CallType,
        assignedDoctor: Doctor?,
        onCallDoctors: List<Doctor>
    ): ActiveCallSession {
        resetActiveJobs()

        val jitsiUrl = AppointmentScheduleHelper.generateEmergencyRoomUrl(patient.id)
        val targetDoctor = assignedDoctor ?: onCallDoctors.firstOrNull { it.onCallStatus == DoctorAvailabilityStatus.AVAILABLE }
        ?: onCallDoctors.firstOrNull()
        ?: Doctor(
            id = "doc_oncall_duty",
            name = "Emergency On-Call Desk",
            specialty = DoctorSpecialty.GENERAL_PHYSICIAN,
            qualification = "MBBS, MD",
            hospitalName = "District Hospital",
            distanceKm = 4.2,
            phone = "108",
            availableDays = "All Days",
            onCallStatus = DoctorAvailabilityStatus.AVAILABLE
        )

        val vitalsSummary = "Condition: ${patient.lastCondition} · Risk: ${patient.currentRiskLevel.name}"

        val session = ActiveCallSession(
            mode = CallMode.EMERGENCY,
            type = callType,
            patientId = patient.id,
            patientName = patient.name,
            patientAge = patient.age,
            villageName = patient.villageName,
            patientVitalsSummary = vitalsSummary,
            doctorId = targetDoctor.id,
            doctorName = targetDoctor.name,
            doctorSpecialty = targetDoctor.specialty.displayName,
            doctorPhone = targetDoctor.phone,
            state = CallSessionState.OUTGOING_RINGING,
            statusMessage = "Calling Dr. ${targetDoctor.name} (Emergency On-Call)…",
            isLowBandwidthMode = callType == CallType.VOICE,
            isCameraOff = callType == CallType.VOICE,
            jitsiRoomUrl = jitsiUrl,
            escalationCount = 0
        )

        _currentSession.value = session
        startDurationTimer()

        // Emergency escalation supervisor
        emergencyEscalationJob = scope.launch {
            // Check if assigned doctor is online and available
            val isAssignedAvailable = targetDoctor.onCallStatus == DoctorAvailabilityStatus.AVAILABLE
            val ringTimeMs = if (isAssignedAvailable) 25_000L else 4_000L

            delay(ringTimeMs)

            // If still ringing (unanswered), escalate to next on-call doctor
            if (_currentSession.value?.state == CallSessionState.OUTGOING_RINGING) {
                escalateEmergencyCall(context, patient, onCallDoctors)
            }
        }

        return session
    }

    /**
     * Escalates to the next available doctor in the on-call queue.
     */
    fun escalateEmergencyCall(
        context: Context? = null,
        patient: Patient,
        onCallDoctors: List<Doctor>
    ) {
        val current = _currentSession.value ?: return
        val nextDoctor = onCallDoctors.firstOrNull {
            it.id != current.doctorId && it.onCallStatus == DoctorAvailabilityStatus.AVAILABLE
        } ?: onCallDoctors.firstOrNull { it.id != current.doctorId }

        if (nextDoctor != null && current.escalationCount < 2) {
            // Escalate to next doctor
            _currentSession.update {
                it?.copy(
                    doctorId = nextDoctor.id,
                    doctorName = nextDoctor.name,
                    doctorSpecialty = nextDoctor.specialty.displayName,
                    doctorPhone = nextDoctor.phone,
                    escalationCount = it.escalationCount + 1,
                    statusMessage = "Doctor unavailable · Re-routing to On-Call Dr. ${nextDoctor.name}…",
                    outcome = EmergencyCallOutcome.ESCALATED_NEXT_DOCTOR
                )
            }

            // Wait 20 seconds for next doctor
            emergencyEscalationJob?.cancel()
            emergencyEscalationJob = scope.launch {
                delay(20_000L)
                if (_currentSession.value?.state == CallSessionState.OUTGOING_RINGING) {
                    fallbackToCellularSms(context, patient)
                }
            }
        } else {
            // Fall back to cellular SMS + GPS
            fallbackToCellularSms(context, patient)
        }
    }

    /**
     * Final emergency fallback: Dispatches SMS + GPS and initiates native phone dialer.
     */
    fun fallbackToCellularSms(context: Context? = null, patient: Patient) {
        emergencyEscalationJob?.cancel()
        _currentSession.update {
            it?.copy(
                state = CallSessionState.FALLBACK_TO_CELLULAR,
                statusMessage = "Couldn't connect — sending SMS for help instead.",
                outcome = EmergencyCallOutcome.FELL_BACK_TO_SMS
            )
        }

        // Auto background SMS with GPS coordinates
        val sosMsg = EmergencySosHelper.createSosMessage(patient)
        context?.let { ctx ->
            EmergencySosHelper.sendCellularSmsFallback(
                context = ctx,
                recipientPhone = patient.emergencyContact,
                message = sosMsg
            )
        }
    }

    /**
     * Simulates doctor receiving an incoming call.
     */
    fun receiveIncomingCall(
        mode: CallMode,
        type: CallType,
        patient: Patient,
        doctor: Doctor
    ) {
        resetActiveJobs()
        val vitalsSummary = "Condition: ${patient.lastCondition} · Risk: ${patient.currentRiskLevel.name}"

        _currentSession.value = ActiveCallSession(
            mode = mode,
            type = type,
            patientId = patient.id,
            patientName = patient.name,
            patientAge = patient.age,
            villageName = patient.villageName,
            patientVitalsSummary = vitalsSummary,
            doctorId = doctor.id,
            doctorName = doctor.name,
            doctorSpecialty = doctor.specialty.displayName,
            state = CallSessionState.INCOMING_RINGING,
            statusMessage = if (mode == CallMode.EMERGENCY) "🚨 INCOMING CRITICAL EMERGENCY CALL" else "Incoming Appointment Call",
            isLowBandwidthMode = type == CallType.VOICE,
            isCameraOff = type == CallType.VOICE,
            jitsiRoomUrl = if (mode == CallMode.EMERGENCY) AppointmentScheduleHelper.generateEmergencyRoomUrl(patient.id) else AppointmentScheduleHelper.generateJitsiRoomUrl(patient.id)
        )
    }

    /**
     * Accepts an incoming call.
     */
    fun acceptCall() {
        emergencyEscalationJob?.cancel()
        _currentSession.update {
            it?.copy(
                state = CallSessionState.CONNECTED,
                statusMessage = "Connected"
            )
        }
        startDurationTimer()
    }

    /**
     * Declines an incoming call.
     */
    fun declineCall() {
        endCall("Call declined by user")
    }

    /**
     * Switches a live video call to voice call (1-tap bandwidth fallback).
     */
    fun switchToVoice() {
        _currentSession.update {
            it?.copy(
                type = CallType.VOICE,
                isCameraOff = true,
                isLowBandwidthMode = true,
                statusMessage = "Switched to voice call (2G Ultra-Low Bandwidth Mode)"
            )
        }
    }

    /**
     * Toggles microphone mute.
     */
    fun toggleMute() {
        _currentSession.update {
            it?.copy(isMuted = !(it.isMuted))
        }
    }

    /**
     * Toggles local video camera.
     */
    fun toggleCamera() {
        _currentSession.update {
            it?.copy(isCameraOff = !(it.isCameraOff))
        }
    }

    /**
     * Simulates network degradation to test proactive low-bandwidth prompts.
     */
    fun simulateNetworkDegradation() {
        _currentSession.update {
            it?.copy(
                state = CallSessionState.POOR_CONNECTION_SUGGEST_VOICE,
                statusMessage = "Weak connection detected · Tap to switch to voice call"
            )
        }
    }

    /**
     * Ends the call and dispatches call log record.
     */
    fun endCall(consultationNotes: String = "") {
        resetActiveJobs()
        val session = _currentSession.value
        if (session != null) {
            val callLog = CallLog(
                id = session.sessionId,
                callType = session.type,
                callMode = session.mode.name,
                patientId = session.patientId,
                patientName = session.patientName,
                doctorId = session.doctorId,
                doctorName = session.doctorName,
                timestamp = System.currentTimeMillis(),
                durationSeconds = session.durationSeconds,
                outcome = session.outcome,
                outcomeNotes = consultationNotes.ifBlank { session.statusMessage }
            )
            onCallCompletedListener?.invoke(callLog)
        }

        _currentSession.value = null
    }

    private fun startDurationTimer() {
        callTimerJob?.cancel()
        callTimerJob = scope.launch {
            while (true) {
                delay(1000)
                _currentSession.update { current ->
                    if (current != null && current.state == CallSessionState.CONNECTED) {
                        current.copy(durationSeconds = current.durationSeconds + 1)
                    } else {
                        current
                    }
                }
            }
        }
    }

    private fun resetActiveJobs() {
        callTimerJob?.cancel()
        emergencyEscalationJob?.cancel()
        callTimerJob = null
        emergencyEscalationJob = null
    }
}
