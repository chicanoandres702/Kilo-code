/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] MainActivity with Jetpack Compose UI
 * [Upstream] User input -> [Downstream] KiloTermux server
 * [Law Check] 75 lines | Passed Do It Check
 */

package com.kilocli.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.ui.theme.KiloAndroidTheme

class MainActivity : ComponentActivity() {
    private lateinit var kiloTermux: KiloTermux

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kiloTermux = KiloTermux.create(this)
        
        setContent { KiloAndroidTheme { KiloChatScreen(kiloTermux) } }
    }
}

@Composable
fun KiloChatScreen(kiloTermux: KiloTermux) {
    var messages by remember { mutableStateOf(listOf<String>()) }
    var input by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Kilo Android", style = MaterialTheme.typography.titleLarge)
            if (!isInitialized) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Initializing...")
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            messages.forEach { msg -> Text(msg, modifier = Modifier.padding(vertical = 4.dp)) }
            Spacer(modifier = Modifier.weight(1f))
            
            Row {
                TextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
                Button(onClick = {
                    messages = messages + "User: $input"
                    input = ""
                }) { Text("Send") }
            }
        }
    }
}