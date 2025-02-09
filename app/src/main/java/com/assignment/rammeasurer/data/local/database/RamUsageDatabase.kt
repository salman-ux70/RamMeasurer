package com.assignment.rammeasurer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assignment.rammeasurer.data.local.dao.RamUsageDao
import com.assignment.rammeasurer.data.local.models.RamUsageEntity

@Database(entities = [RamUsageEntity::class], version = 1, exportSchema = false)
abstract class RamUsageDatabase : RoomDatabase() {

    abstract fun ramUsageDao(): RamUsageDao


}