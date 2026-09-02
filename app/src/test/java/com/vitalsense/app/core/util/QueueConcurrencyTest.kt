package com.vitalsense.app.core.util

import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.data.model.QueueEntrySource
import com.vitalsense.app.core.data.model.QueueEntryStatus
import org.junit.Assert.assertThrows
import org.junit.Test

class QueueConcurrencyTest {

    private fun startConsultationValidator(
        targetEntryId: String,
        currentEntries: List<QueueEntry>
    ) {
        val entry = currentEntries.find { it.id == targetEntryId }
            ?: throw IllegalArgumentException("Entry not found")

        val existingActive = currentEntries.find {
            it.doctorId == entry.doctorId &&
                it.dateFormatted == entry.dateFormatted &&
                it.status == QueueEntryStatus.IN_CONSULTATION &&
                it.id != targetEntryId
        }

        if (existingActive != null) {
            throw IllegalStateException("Another consultation is already in progress with ${existingActive.patientName} (Token #${existingActive.tokenNumber}).")
        }
    }

    @Test
    fun testStartConsultation_throwsWhenAnotherIsActive() {
        val activeConsultation = QueueEntry(
            id = "active_1",
            doctorId = "doc_1",
            doctorName = "Dr. Sharma",
            dateFormatted = "2026-09-02",
            tokenNumber = 1,
            provisionalToken = false,
            patientId = "pat_1",
            patientName = "Sita Devi",
            source = QueueEntrySource.WALK_IN,
            status = QueueEntryStatus.IN_CONSULTATION,
            priorityFlag = false,
            checkedInAt = 1000L,
            consultationStartedAt = 2000L
        )

        val waitingPatient = QueueEntry(
            id = "waiting_2",
            doctorId = "doc_1",
            doctorName = "Dr. Sharma",
            dateFormatted = "2026-09-02",
            tokenNumber = 2,
            provisionalToken = false,
            patientId = "pat_2",
            patientName = "Ramesh Kumar",
            source = QueueEntrySource.WALK_IN,
            status = QueueEntryStatus.WAITING,
            priorityFlag = false,
            checkedInAt = 1500L
        )

        val entries = listOf(activeConsultation, waitingPatient)

        assertThrows(IllegalStateException::class.java) {
            startConsultationValidator("waiting_2", entries)
        }
    }
}
