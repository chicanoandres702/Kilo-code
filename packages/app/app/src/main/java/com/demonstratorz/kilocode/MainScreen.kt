/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #22
 * [Subtask] Remove terminal fallback from navigation
 * [Upstream] MainScreen -> [Downstream] Chat, Files, and Settings
 * [Law Check] 75 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kilocli.android.InstallState
import com.kilocli.android.KiloTermux
import com.kilocli.android.SettingsState

@Composable
fun MainScreen(kiloTermux: KiloTermux, installState: InstallState, onRetry: () -> Unit) {
    val settingsState = remember { SettingsState() }
    var currentScreen by remember { mutableStateOf(0) }
    val isReady = installState.isComplete && installState.status == "Ready"

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(24.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(icon = { Icon(Icons.Default.Info, "Chat") }, label = { Text("Chat") }, selected = currentScreen == 0, onClick = { currentScreen = 0 })
                NavigationBarItem(icon = { Icon(Icons.AutoMirrored.Filled.List, "Files") }, label = { Text("Files") }, selected = currentScreen == 1, onClick = { currentScreen = 1 })
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, "Settings") }, label = { Text("Settings") }, selected = currentScreen == 2, onClick = { currentScreen = 2 })
            }
        }
    ) { padding ->
        MainContent(currentScreen, kiloTermux, installState, settingsState, isReady, onRetry, Modifier.padding(padding))
    }
}

@Composable
private fun MainContent(
    currentScreen: Int,
    kiloTermux: KiloTermux,
    installState: InstallState,
    settingsState: SettingsState,
    isReady: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier
) {
    when (currentScreen) {
        0 -> KiloChatScreen(kiloTermux, installState, settingsState, modifier)
        1 -> FileExplorer(kiloTermux, installState, modifier)
        2 -> SettingsScreen(settingsState, modifier)
    }

    if (!isReady) InstallOverlay(installState, onRetry)
}
