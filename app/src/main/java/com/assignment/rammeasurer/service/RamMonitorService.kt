package com.assignment.rammeasurer.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.assignment.rammeasurer.IRamUsageCallback
import com.assignment.rammeasurer.IRamUsageService
import com.assignment.rammeasurer.R
import com.assignment.rammeasurer.data.local.models.RamUsageEntity
import com.assignment.rammeasurer.data.repo.RamUsageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.RandomAccessFile
import javax.inject.Inject

@AndroidEntryPoint
class RamMonitorService : Service() {
    @Inject
    lateinit var repository: RamUsageRepository


    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    private var callback: IRamUsageCallback? = null

    private val binder = object : IRamUsageService.Stub(){
        override fun registerCallback(cb: IRamUsageCallback?) {
            callback = cb
        }

        override fun unregisterCallback() {
            callback = null
        }

    }



    override fun onBind(p0: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startRamMonitoring()

    }

    private fun saveRamUsageToDatabase() {
        val (totalRam, usedRam) = fetchRamUsage()
        val availableRam = totalRam - usedRam

        CoroutineScope(Dispatchers.IO).launch {
            val ramUsage = RamUsageEntity(
                time = RamUsageEntity.getCurrentTime(),
                ramUsed = usedRam,
                ramAvailable = availableRam
            )
            repository.insertRamUsage(ramUsage)
        }
    }

    private fun startRamMonitoring() {
        isMonitoring = true
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isMonitoring) return
                sendRamUsageToClient()
                saveRamUsageToDatabase()
                handler.postDelayed(this, /*5 * */10 * 1000) // Repeat every 5 minutes
            }
        }, 0)
    }

    private fun sendRamUsageToClient() {
        val (totalRam, usedRam) = fetchRamUsage()
        callback?.onRamUsageUpdated(totalRam, usedRam)
    }

    private fun startForegroundService(){
        val channelId = "RAM_MONITOR_CHANNEL"
        createNotificationChannel(channelId)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("RAM Monitor")
            .setContentText("Monitoring RAM usage...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // Ensure this icon exists
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(1, notification) // This is required for foreground service
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "RAM Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors RAM usage in the background"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun fetchRamUsage(): Pair<Int,Int> {
        val memInfo = ActivityManager.MemoryInfo()
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memInfo)
        val totalRam = (getTotalRAM() / (1024 * 1024)).toInt()  // Convert to MB
        val usedRam = (totalRam - (memInfo.availMem / (1024 * 1024))).toInt()
        return Pair(totalRam, usedRam)
    }

    private fun getTotalRAM(): Long {
        return try {
            val reader = RandomAccessFile("/proc/meminfo", "r")
            val line = reader.readLine()
            reader.close()
            val split = line.split("\\s+".toRegex())
            split[1].toLong() * 1024  // Convert to bytes
        } catch (e: Exception) {
            0L
        }
    }


}