package com.vitalsense.app.core.call

import com.vitalsense.app.core.data.model.*
import org.junit.Assert.*
import org.junit.Test

class TeleCallingManagerTest {

    @Test
    fun testAppointmentJoinGraceWindow() {
        val scheduledTimeMs = 1_000_000L
        val appointment = Appointment(
            id = "appt_test_1",
            patientId = "pat_1",
            patientName = "Ramesh Kumar",
            doctorId = "doc_1",
            doctorName = "Dr. Rajesh Varma",
            doctorSpecialty = "General Physician",
            dateFormatted = "2026-09-03",
            timeSlot = "10:00 AM",
            status = "Confirmed",
            proposedBy = UserRole.PATIENT,
            callType = CallType.VIDEO,
            scheduledTimestamp = scheduledTimeMs
        )

        // 1. 25 minutes before scheduled time -> Outside window (BEFORE_WINDOW)
        val tMinus25 = scheduledTimeMs - (25 * 60 * 1000L)
        val statusBefore = AppointmentScheduleHelper.evaluateJoinWindow(appointment, tMinus25)
        assertEquals(JoinWindowStatus.BEFORE_WINDOW, statusBefore)

        // 2. 8 minutes before scheduled time -> Inside -10m window (JOIN_ACTIVE)
        val tMinus8 = scheduledTimeMs - (8 * 60 * 1000L)
        val statusActiveBefore = AppointmentScheduleHelper.evaluateJoinWindow(appointment, tMinus8)
        assertEquals(JoinWindowStatus.JOIN_ACTIVE, statusActiveBefore)

        // 3. Exactly on scheduled time -> Inside window (JOIN_ACTIVE)
        val statusExact = AppointmentScheduleHelper.evaluateJoinWindow(appointment, scheduledTimeMs)
        assertEquals(JoinWindowStatus.JOIN_ACTIVE, statusExact)

        // 4. 12 minutes after scheduled time -> Inside +15m grace window (JOIN_ACTIVE)
        val tPlus12 = scheduledTimeMs + (12 * 60 * 1000L)
        val statusActiveAfter = AppointmentScheduleHelper.evaluateJoinWindow(appointment, tPlus12)
        assertEquals(JoinWindowStatus.JOIN_ACTIVE, statusActiveAfter)

        // 5. 25 minutes after scheduled time -> Grace window exceeded (AFTER_WINDOW_MISSED)
        val tPlus25 = scheduledTimeMs + (25 * 60 * 1000L)
        val statusMissed = AppointmentScheduleHelper.evaluateJoinWindow(appointment, tPlus25)
        assertEquals(JoinWindowStatus.AFTER_WINDOW_MISSED, statusMissed)
    }

    @Test
    fun testEmergencyCallEscalationRouting() {
        val patient = Patient(
            id = "pat_emergency_1",
            name = "Kavita Devi",
            age = 29,
            gender = "F",
            phone = "9876543210",
            villageId = "vil_sundarpura",
            villageName = "Sundarpura",
            ashaWorkerId = "asha_1",
            ashaWorkerName = "Sarita Devi",
            currentRiskLevel = SeverityLevel.MODERATE,
            lastCondition = "Emergency fever",
            lastVisitDate = "Today",
            nextAppointmentDate = null,
            emergencyContact = "9876543210"
        )

        val assignedDoctor = Doctor(
            id = "doc_assigned",
            name = "Dr. Assigned (Busy)",
            specialty = DoctorSpecialty.GENERAL_PHYSICIAN,
            qualification = "MBBS",
            hospitalName = "PHC Sundarpura",
            distanceKm = 1.0,
            phone = "111",
            availableDays = "All Days",
            onCallStatus = DoctorAvailabilityStatus.BUSY
        )

        val onCallDoctor1 = Doctor(
            id = "doc_oncall_1",
            name = "Dr. OnCall Sunita",
            specialty = DoctorSpecialty.CARDIOLOGIST,
            qualification = "MD",
            hospitalName = "District Hospital",
            distanceKm = 5.0,
            phone = "222",
            availableDays = "All Days",
            onCallStatus = DoctorAvailabilityStatus.AVAILABLE
        )

        val onCallList = listOf(assignedDoctor, onCallDoctor1)

        // Step 1: Start emergency call targeting assigned doctor
        val session = TeleCallingManager.startEmergencyCall(
            context = null,
            patient = patient,
            callType = CallType.VIDEO,
            assignedDoctor = assignedDoctor,
            onCallDoctors = onCallList
        )

        assertEquals(CallMode.EMERGENCY, session.mode)
        assertEquals(CallType.VIDEO, session.type)
        assertEquals(CallSessionState.OUTGOING_RINGING, session.state)
        assertEquals("doc_assigned", session.doctorId)
        assertEquals(0, session.escalationCount)

        // Step 2: Escalate because assigned doctor was unavailable/busy
        TeleCallingManager.escalateEmergencyCall(
            context = null,
            patient = patient,
            onCallDoctors = onCallList
        )

        val escalatedSession = TeleCallingManager.currentSession.value
        assertNotNull(escalatedSession)
        assertEquals("doc_oncall_1", escalatedSession?.doctorId)
        assertEquals("Dr. OnCall Sunita", escalatedSession?.doctorName)
        assertEquals(1, escalatedSession?.escalationCount)
        assertEquals(EmergencyCallOutcome.ESCALATED_NEXT_DOCTOR, escalatedSession?.outcome)

        // Step 3: All on-call doctors exhausted -> Fallback to cellular SMS+GPS
        TeleCallingManager.fallbackToCellularSms(
            context = null,
            patient = patient
        )

        val fallbackSession = TeleCallingManager.currentSession.value
        assertNotNull(fallbackSession)
        assertEquals(CallSessionState.FALLBACK_TO_CELLULAR, fallbackSession?.state)
        assertEquals(EmergencyCallOutcome.FELL_BACK_TO_SMS, fallbackSession?.outcome)
        assertTrue(fallbackSession?.statusMessage?.contains("SMS", ignoreCase = true) == true)
    }

    @Test
    fun testOneTapSwitchToVoiceCallDuringVideo() {
        val appointment = Appointment(
            id = "appt_vid_1",
            patientId = "pat_2",
            patientName = "Suresh Patel",
            doctorId = "doc_2",
            doctorName = "Dr. Sunita Gupta",
            doctorSpecialty = "Pediatrician",
            dateFormatted = "Today",
            timeSlot = "11:00 AM",
            status = "Confirmed",
            proposedBy = UserRole.DOCTOR,
            callType = CallType.VIDEO
        )

        TeleCallingManager.startAppointmentCall(appointment, isDoctor = true)
        val initialSession = TeleCallingManager.currentSession.value

        assertNotNull(initialSession)
        assertEquals(CallType.VIDEO, initialSession?.type)
        assertFalse(initialSession!!.isLowBandwidthMode)
        assertFalse(initialSession.isCameraOff)

        // User taps "Switch to Voice" button
        TeleCallingManager.switchToVoice()
        val voiceSession = TeleCallingManager.currentSession.value

        assertNotNull(voiceSession)
        assertEquals(CallType.VOICE, voiceSession?.type)
        assertTrue(voiceSession!!.isLowBandwidthMode)
        assertTrue(voiceSession.isCameraOff)
    }

    @Test
    fun testCallLogPersistenceSerialization() {
        var recordedLog: CallLog? = null
        TeleCallingManager.onCallCompletedListener = { log ->
            recordedLog = log
        }

        val appointment = Appointment(
            id = "appt_log_test",
            patientId = "pat_9",
            patientName = "Amit Sharma",
            doctorId = "doc_5",
            doctorName = "Dr. Anil Mehta",
            doctorSpecialty = "Orthopedic",
            dateFormatted = "Today",
            timeSlot = "02:00 PM",
            status = "Confirmed",
            proposedBy = UserRole.PATIENT,
            callType = CallType.VOICE
        )

        TeleCallingManager.startAppointmentCall(appointment, isDoctor = true)
        TeleCallingManager.endCall("Patient advised joint exercises and calcium supplement.")

        assertNotNull(recordedLog)
        assertEquals("pat_9", recordedLog?.patientId)
        assertEquals("Amit Sharma", recordedLog?.patientName)
        assertEquals("doc_5", recordedLog?.doctorId)
        assertEquals(CallType.VOICE, recordedLog?.callType)
        assertEquals("APPOINTMENT", recordedLog?.callMode)
        assertEquals(EmergencyCallOutcome.CONNECTED, recordedLog?.outcome)
        assertEquals("Patient advised joint exercises and calcium supplement.", recordedLog?.outcomeNotes)
    }
}
