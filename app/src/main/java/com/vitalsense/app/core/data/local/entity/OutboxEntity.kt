package com.vitalsense.app.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Durable outbox record for storing pending mutations when the device is offline.
 * Flushed automatically by WorkManager upon network reconnection (Section 3 & 4 of System Design).
 */
@Entity(tableName = "outbox_records")
data class OutboxEntity(
    @PrimaryKey val id: String,
    val actionType: String, // "CONDITION_RECORD", "PRESCRIPTION", "APPOINTMENT", "BROADCAST_NOTICE", "PATIENT"
    val entityId: String,
    val payloadJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)
