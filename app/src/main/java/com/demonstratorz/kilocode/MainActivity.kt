/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #1
 * [Subtask] Implement runtime permission request for notifications
 * [Upstream] MainActivity.onCreate -> [Downstream] KiloServerService
 * [Law Check] 58 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.demonstratorz.kilocode.ui.theme.KiloAndroidTheme

class MainActivity : ComponentActivity() {
    private lateinit var kiloTermux: KiloTermux
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        startKiloService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kiloTermux = KiloTermux.create(this)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                startKiloService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startKiloService()
        }
        
        setContent { KiloAndroidTheme { KiloChatScreen(kiloTermux) } }
    }

    private fun startKiloService() {
        val serviceIntent = Intent(this, KiloServerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}