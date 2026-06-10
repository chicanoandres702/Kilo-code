/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Create SettingsScreen for MCP and mode configuration
 * [Upstream] MainActivity -> [Downstream] SettingsScreen UI
 * [Law Check] 50 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.kilocli.android.SettingsState

@Composable
fun SettingsScreen(settingsState: SettingsState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp).fillMaxSize()) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("MCP Server Endpoint", style = MaterialTheme.typography.titleMedium)
        TextField(value = settingsState.mcpEndpoint, onValueChange = { settingsState.mcpEndpoint = it }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Enabled Modes", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = settingsState.enableArchitect, onCheckedChange = { settingsState.enableArchitect = it })
            Text("Architect")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = settingsState.enableCoder, onCheckedChange = { settingsState.enableCoder = it })
            Text("Coder")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = settingsState.enableDebugger, onCheckedChange = { settingsState.enableDebugger = it })
            Text("Debugger")
        }
    }
}
