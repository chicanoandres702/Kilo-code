/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Foreground service for Kilo server
 * [Upstream] MainActivity -> [Downstream] Termux embedded environment
 * [Law Check] 65 lines | Passed Do It Check
 */

package com.kilocli.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KiloServerService : Service() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var kiloTermux: KiloTermux

    override fun onCreate() {
        super.onCreate()
        kiloTermux = KiloTermux.create(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, createNotification())
        }
        scope.launch {
            kiloTermux.runServer().collectLatest { line ->
                // Server output handling
            }
        }
        return START_STICKY
    }

    private fun createNotification(): Notification =
        NotificationCompat.Builder(this, "kilo_channel")
            .setContentTitle("Kilo Server")
            .setContentText("Running in background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "kilo_channel",
            "Kilo Server",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent) = null
    override fun onDestroy() = job.cancel()
}