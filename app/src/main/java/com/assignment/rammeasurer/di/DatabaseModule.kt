package com.assignment.rammeasurer.di

import android.content.Context
import androidx.room.Room
import com.assignment.rammeasurer.data.local.dao.RamUsageDao
import com.assignment.rammeasurer.data.local.database.RamUsageDatabase
import com.assignment.rammeasurer.data.repo.RamUsageRepository
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
    fun provideDatabase(@ApplicationContext context: Context): RamUsageDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RamUsageDatabase::class.java,
            "ram_usage_database"
        ).build()
    }

    @Provides
    fun provideDao(database: RamUsageDatabase): RamUsageDao {
        return database.ramUsageDao()
    }

    @Provides
    @Singleton
    fun provideRepository(dao: RamUsageDao): RamUsageRepository {
        return RamUsageRepository(dao)
    }
}