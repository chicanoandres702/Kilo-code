/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #1
 * [Subtask] Improve service robustness and startup safety
 * [Upstream] MainActivity -> [Downstream] Termux embedded environment
 * [Law Check] 83 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KiloServerService : Service() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var kiloTermux: KiloTermux? = null

    override fun onCreate() {
        super.onCreate()
        try {
            kiloTermux = KiloTermux.create(this)
            createNotificationChannel()
        } catch (e: Exception) {
            Log.e("KiloServerService", "Initialization failed: ${e.message}")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (kiloTermux == null) {
            return START_NOT_STICKY
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, createNotification())
        }
        
        scope.launch {
            kiloTermux?.runServer()?.collectLatest { line ->
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "kilo_channel",
                "Kilo Server",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent) = null
    override fun onDestroy() = job.cancel()
}