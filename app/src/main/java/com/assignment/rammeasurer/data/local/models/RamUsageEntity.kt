package com.assignment.rammeasurer.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "ram_usage")
data class RamUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: String,
    val ramUsed: Int,
    val ramAvailable: Int
){
    companion object {
        fun getCurrentTime(): String {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
