package com.vitalsense.app.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        OutboxEntity::class,
        ImmunizationRecordEntity::class,
        DailyRoundEntity::class,
        AshaMedicineEntity::class,
        DiseaseTrendRecordEntity::class,
        LabReportEntity::class,
        OpdTokenEntity::class,
        MedicalCertificateEntity::class,
        BloodStockEntity::class,
        IpdBedEntity::class,
        OtSurgeryBookingEntity::class,
        ExternalReferralEntity::class,
        BioMedicalEquipmentEntity::class,
        DoctorDaySlotEntity::class,
        QueueEntryEntity::class,
        NearbyPharmacyCacheEntity::class,
        CallLogEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VitalSenseDatabase : RoomDatabase() {

    abstract fun vitalSenseDao(): VitalSenseDao

    companion object {
        @Volatile
        private var INSTANCE: VitalSenseDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `doctor_day_slots` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `doctorId` TEXT NOT NULL,
                        `dateFormatted` TEXT NOT NULL,
                        `startTime` TEXT NOT NULL,
                        `endTime` TEXT NOT NULL,
                        `capacity` INTEGER NOT NULL,
                        `isWalkInOpen` INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `queue_entries` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `doctorId` TEXT NOT NULL,
                        `doctorName` TEXT NOT NULL,
                        `dateFormatted` TEXT NOT NULL,
                        `tokenNumber` INTEGER NOT NULL,
                        `provisionalToken` INTEGER NOT NULL,
                        `appointmentId` TEXT,
                        `patientId` TEXT NOT NULL,
                        `patientName` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `priorityFlag` INTEGER NOT NULL,
                        `checkedInAt` INTEGER NOT NULL,
                        `calledAt` INTEGER,
                        `consultationStartedAt` INTEGER,
                        `completedAt` INTEGER,
                        `outcomeNotes` TEXT,
                        `isPendingSync` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `nearby_pharmacy_cache` (
                        `placeId` TEXT NOT NULL PRIMARY KEY,
                        `name` TEXT NOT NULL,
                        `address` TEXT NOT NULL,
                        `latitude` REAL NOT NULL,
                        `longitude` REAL NOT NULL,
                        `phoneNumber` TEXT,
                        `cachedAt` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `appointments` ADD COLUMN `callType` TEXT NOT NULL DEFAULT 'VIDEO'")
                db.execSQL("ALTER TABLE `appointments` ADD COLUMN `scheduledTimestamp` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `call_logs` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `callType` TEXT NOT NULL,
                        `callMode` TEXT NOT NULL,
                        `patientId` TEXT NOT NULL,
                        `patientName` TEXT NOT NULL,
                        `doctorId` TEXT NOT NULL,
                        `doctorName` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `outcome` TEXT NOT NULL,
                        `outcomeNotes` TEXT
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): VitalSenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitalSenseDatabase::class.java,
                    "vitalsense_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
