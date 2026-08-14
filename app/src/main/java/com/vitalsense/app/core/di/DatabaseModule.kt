package com.vitalsense.app.core.di

import android.content.Context
import com.vitalsense.app.core.data.local.VitalSenseDatabase
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import com.vitalsense.app.core.data.repository.VitalSenseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVitalSenseDatabase(
        @ApplicationContext context: Context
    ): VitalSenseDatabase {
        return VitalSenseDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideVitalSenseRepository(
        database: VitalSenseDatabase
    ): VitalSenseRepository {
        return VitalSenseRepositoryImpl(database)
    }
}
