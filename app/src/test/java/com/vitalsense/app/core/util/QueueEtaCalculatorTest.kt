package com.vitalsense.app.core.util

import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.data.model.QueueEntrySource
import com.vitalsense.app.core.data.model.QueueEntryStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class QueueEtaCalculatorTest {

    private fun createCompletedEntry(id: String, durationSeconds: Long): QueueEntry {
        val start = 1000000L
        val end = start + (durationSeconds * 1000L)
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
            status = QueueEntryStatus.COMPLETED,
            priorityFlag = false,
            checkedInAt = start - 60000L,
            consultationStartedAt = start,
            completedAt = end
        )
    }

    private fun createWaitingEntry(id: String, checkedInAt: Long, priority: Boolean = false): QueueEntry {
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
    fun testAverageConsultationSeconds_fallsBackToDefaultWhenEmpty() {
        val avg = QueueEtaCalculator.averageConsultationSeconds(emptyList(), emptyList())
        assertEquals(QueueEtaCalculator.DEFAULT_AVG_CONSULTATION_SECONDS, avg)
    }

    @Test
    fun testAverageConsultationSeconds_fallsBackTo7DayWhenTodayHasFewerThan3Samples() {
        val today = listOf(
            createCompletedEntry("t1", 300L),
            createCompletedEntry("t2", 400L)
        )
        val fallback = listOf(
            createCompletedEntry("f1", 700L),
            createCompletedEntry("f2", 800L),
            createCompletedEntry("f3", 900L)
        )
        val avg = QueueEtaCalculator.averageConsultationSeconds(today, fallback)
        assertEquals(800L, avg)
    }

    @Test
    fun testAverageConsultationSeconds_usesTodayWhenAtLeast3Samples() {
        val today = listOf(
            createCompletedEntry("t1", 300L),
            createCompletedEntry("t2", 400L),
            createCompletedEntry("t3", 500L)
        )
        val fallback = listOf(
            createCompletedEntry("f1", 1000L)
        )
        val avg = QueueEtaCalculator.averageConsultationSeconds(today, fallback)
        assertEquals(400L, avg)
    }

    @Test
    fun testCalculatePositionAndWaitTime() {
        val entries = listOf(
            createWaitingEntry("p1", 100L),
            createWaitingEntry("p2", 200L),
            createWaitingEntry("p3", 300L)
        )

        val pos0 = QueueEtaCalculator.calculatePosition("p1", entries)
        val pos2 = QueueEtaCalculator.calculatePosition("p3", entries)

        assertEquals(0, pos0)
        assertEquals(2, pos2)

        val waitSec = QueueEtaCalculator.calculateWaitTimeSeconds(pos2, 600L)
        assertEquals(1200L, waitSec)
    }

    @Test
    fun testFormatEta() {
        assertEquals("< 2 min", QueueEtaCalculator.formatEta(60L))
        assertEquals("~10 min", QueueEtaCalculator.formatEta(600L))
        assertEquals("~15 min", QueueEtaCalculator.formatEta(850L))
    }
}
