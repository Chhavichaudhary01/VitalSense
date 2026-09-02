package com.vitalsense.app.core.util

import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.data.model.QueueEntrySource
import com.vitalsense.app.core.data.model.QueueEntryStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class QueueOrderingTest {

    private fun makeEntry(id: String, checkedInAt: Long, priority: Boolean): QueueEntry {
        return QueueEntry(
            id = id,
            doctorId = "doc_1",
            doctorName = "Dr. Rajesh Sharma",
            dateFormatted = "2026-09-02",
            tokenNumber = 1,
            provisionalToken = false,
            patientId = "pat_$id",
            patientName = "Patient $id",
            source = QueueEntrySource.WALK_IN,
            status = QueueEntryStatus.WAITING,
            priorityFlag = priority,
            checkedInAt = checkedInAt
        )
    }

    @Test
    fun testPriorityEntriesSortFirstAndOrderedByCheckIn() {
        val entryRegularEarly = makeEntry("regular_early", checkedInAt = 1000L, priority = false)
        val entryRegularLate = makeEntry("regular_late", checkedInAt = 3000L, priority = false)
        val entryPriorityEarly = makeEntry("priority_early", checkedInAt = 2000L, priority = true)
        val entryPriorityLate = makeEntry("priority_late", checkedInAt = 4000L, priority = true)

        val mixed = listOf(entryRegularEarly, entryPriorityLate, entryRegularLate, entryPriorityEarly)
        val sorted = QueueEtaCalculator.sortWaitingEntries(mixed)

        // Expected order: priority_early (2000), priority_late (4000), regular_early (1000), regular_late (3000)
        assertEquals("priority_early", sorted[0].id)
        assertEquals("priority_late", sorted[1].id)
        assertEquals("regular_early", sorted[2].id)
        assertEquals("regular_late", sorted[3].id)
    }
}
