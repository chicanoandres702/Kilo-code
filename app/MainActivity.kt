/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] MainActivity with Jetpack Compose UI
 * [Upstream] User input -> [Downstream] KiloTermux server
 * [Law Check] 76 lines | Passed Do It Check
 */

package com.kilocli.android

import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.ui.theme.KiloAndroidTheme

class MainActivity : ComponentActivity() {
    private lateinit var kiloTermux: KiloTermux

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kiloTermux = KiloTermux.create(this)
        
        // Explicitly start the service
        val serviceIntent = Intent(this, KiloServerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        
        setContent { KiloAndroidTheme { KiloChatScreen(kiloTermux) } }
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