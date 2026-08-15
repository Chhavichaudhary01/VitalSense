package com.vitalsense.app.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vitalsense.app.core.data.local.dao.VitalSenseDao
import com.vitalsense.app.core.data.local.entity.*
import com.vitalsense.app.core.data.local.typeconverters.Converters

@Database(
    entities = [
        VillageEntity::class,
        PatientEntity::class,
        AshaWorkerEntity::class,
        DoctorEntity::class,
        ConditionRecordEntity::class,
        PrescriptionEntity::class,
        AppointmentEntity::class,
        BroadcastNoticeEntity::class,
        DispensaryEntity::class,
        GovernmentSchemeEntity::class,
        OutboxEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VitalSenseDatabase : RoomDatabase() {

    abstract fun vitalSenseDao(): VitalSenseDao

    companion object {
        @Volatile
        private var INSTANCE: VitalSenseDatabase? = null

        fun getDatabase(context: Context): VitalSenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitalSenseDatabase::class.java,
                    "vitalsense_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
