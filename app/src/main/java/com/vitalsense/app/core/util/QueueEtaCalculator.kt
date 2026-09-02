package com.vitalsense.app.core.util

import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.data.model.QueueEntryStatus

/**
 * Pure functions for Queue ETA and ordering calculations as specified in Section 3.2 and 3.3.
 * Fully unit-testable in isolation without Android framework dependencies.
 */
object QueueEtaCalculator {

    const val DEFAULT_AVG_CONSULTATION_SECONDS = 600L // 10 minutes fallback
    private const val SAMPLE_LIMIT = 15
    private const val MIN_TODAY_SAMPLES = 3

    /**
     * Calculates average consultation duration in seconds:
     * 1. Mean over the last 15 COMPLETED entries today.
     * 2. Falls back to recent 7-day completed entries if today has < 3 samples.
     * 3. Falls back to hardcoded default (600 seconds) if no historical samples exist.
     */
    fun averageConsultationSeconds(
        todayCompleted: List<QueueEntry>,
        fallbackCompleted: List<QueueEntry> = emptyList()
    ): Long {
        val validToday = todayCompleted
            .filter { it.status == QueueEntryStatus.COMPLETED && it.completedAt != null && it.consultationStartedAt != null }
            .takeLast(SAMPLE_LIMIT)
            .map { (it.completedAt!! - it.consultationStartedAt!!) / 1000L }
            .filter { it > 0 }

        if (validToday.size >= MIN_TODAY_SAMPLES) {
            return validToday.average().toLong()
        }

        val validFallback = fallbackCompleted
            .filter { it.status == QueueEntryStatus.COMPLETED && it.completedAt != null && it.consultationStartedAt != null }
            .takeLast(SAMPLE_LIMIT)
            .map { (it.completedAt!! - it.consultationStartedAt!!) / 1000L }
            .filter { it > 0 }

        if (validFallback.isNotEmpty()) {
            return validFallback.average().toLong()
        }

        if (validToday.isNotEmpty()) {
            return validToday.average().toLong()
        }

        return DEFAULT_AVG_CONSULTATION_SECONDS
    }

    /**
     * Sorts waiting entries: priorityFlag == true first (ordered by checkedInAt),
     * followed by regular entries (ordered by checkedInAt).
     */
    fun sortWaitingEntries(entries: List<QueueEntry>): List<QueueEntry> {
        val (prioritized, regular) = entries
            .filter { it.status == QueueEntryStatus.WAITING }
            .partition { it.priorityFlag }

        return prioritized.sortedBy { it.checkedInAt } + regular.sortedBy { it.checkedInAt }
    }

    /**
     * Computes the 0-indexed position of an entry among sorted waiting entries ahead of it.
     * e.g., 0 means you're next, 3 means 3 people ahead of you.
     */
    fun calculatePosition(targetEntryId: String, sortedWaiting: List<QueueEntry>): Int {
        val index = sortedWaiting.indexOfFirst { it.id == targetEntryId }
        return if (index >= 0) index else 0
    }

    /**
     * Computes estimated wait time in seconds.
     */
    fun calculateWaitTimeSeconds(position: Int, avgConsultationSeconds: Long): Long {
        return position * avgConsultationSeconds
    }

    /**
     * Formats seconds into human-readable minutes string (e.g. "~12 min", "< 2 min").
     */
    fun formatEta(waitSeconds: Long): String {
        val minutes = (waitSeconds + 59) / 60
        return if (minutes <= 1) "< 2 min" else "~$minutes min"
    }
}
