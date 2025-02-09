package com.assignment.rammeasurer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.assignment.rammeasurer.data.local.models.RamUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RamUsageDao {

    @Insert
    suspend fun insertRamUsage(ramUsage: RamUsageEntity)

    @Query("SELECT * FROM ram_usage ORDER BY id DESC")
    fun getAllRamUsage(): Flow<List<RamUsageEntity>>

    @Query("DELETE FROM ram_usage")
    suspend fun clearAllData()
}