/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #1
 * [Subtask] Implement runtime permission request for notifications
 * [Upstream] MainActivity.onCreate -> [Downstream] KiloServerService
 * [Law Check] 58 lines | Passed Do It Check
 */

package com.kilocli.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.kilocli.android.ui.theme.KiloAndroidTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiloChatScreen(kiloTermux: KiloTermux) {
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var input by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kiloTermux.initialize().collect { state ->
            if (state.isComplete) {
                isInitialized = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kilo Android") },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!isInitialized) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Initializing Termux...", modifier = Modifier.padding(8.dp))
            }
            
            LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                items(messages) { msg ->
                    MessageBubble(text = msg.first, isUser = msg.second)
                }
            }
            
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
                Button(onClick = {
                    messages = messages + Pair(input, true)
                    input = ""
                }, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }
            }
        }
    }
}