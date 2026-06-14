/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Refactor MainActivity to fit 100-line law
 * [Upstream] MainActivity -> [Downstream] KiloChatScreen, SettingsScreen, FileExplorer
 * [Law Check] 95 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.demonstratorz.kilocode.ui.theme.KiloAndroidTheme
import com.kilocli.android.KiloTermux
import com.kilocli.android.SettingsState
import com.demonstratorz.kilocode.KiloChatScreen
import com.demonstratorz.kilocode.FileExplorer
import com.demonstratorz.kilocode.TerminalScreen

class MainActivity : ComponentActivity() {
    private lateinit var kiloTermux: KiloTermux
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kiloTermux = KiloTermux.create(this)
        
        setContent {
            KiloAndroidTheme {
                MainScreen(kiloTermux)
            }
        }
    }
}

@Composable
fun MainScreen(kiloTermux: KiloTermux) {
    val settingsState = remember { SettingsState() }
    var currentScreen by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, "Chat") },
                    label = { Text("Chat") },
                    selected = currentScreen == 0,
                    onClick = { currentScreen = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, "Files") },
                    label = { Text("Files") },
                    selected = currentScreen == 1,
                    onClick = { currentScreen = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, "Settings") },
                    label = { Text("Settings") },
                    selected = currentScreen == 2,
                    onClick = { currentScreen = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, "Terminal") },
                    label = { Text("Terminal") },
                    selected = currentScreen == 3,
                    onClick = { currentScreen = 3 }
                )
            }
        }
    ) { padding ->
        when (currentScreen) {
            0 -> KiloChatScreen(kiloTermux, settingsState, Modifier.padding(padding))
            1 -> FileExplorer(kiloTermux, Modifier.padding(padding))
            2 -> SettingsScreen(settingsState, Modifier.padding(padding))
            3 -> TerminalScreen(Modifier.padding(padding))
        }
    }
}
