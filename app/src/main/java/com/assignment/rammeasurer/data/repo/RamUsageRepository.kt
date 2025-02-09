package com.assignment.rammeasurer.data.repo

import com.assignment.rammeasurer.data.local.dao.RamUsageDao
import com.assignment.rammeasurer.data.local.models.RamUsageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RamUsageRepository @Inject constructor(private val ramUsageDao: RamUsageDao) {

    fun getData() = ramUsageDao.getAllRamUsage()
    val allRamUsage: Flow<List<RamUsageEntity>> = ramUsageDao.getAllRamUsage()

    suspend fun insertRamUsage(ramUsage: RamUsageEntity) {
        ramUsageDao.insertRamUsage(ramUsage)
    }
}