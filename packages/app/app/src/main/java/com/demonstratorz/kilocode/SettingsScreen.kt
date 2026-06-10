/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Create modern SettingsScreen cards
 * [Upstream] MainActivity -> [Downstream] SettingsScreen UI
 * [Law Check] 93 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.SettingsState

@Composable
fun SettingsScreen(settingsState: SettingsState, modifier: Modifier = Modifier) {
    var endpoint by remember { mutableStateOf(settingsState.mcpEndpoint) }
    val modes = remember {
        mutableStateMapOf(
            "Architect" to settingsState.enableArchitect,
            "Coder" to settingsState.enableCoder,
            "Debugger" to settingsState.enableDebugger
        )
    }

    Column(modifier = modifier.padding(16.dp).fillMaxSize()) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Tune your Kilo experience", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        SettingCard("MCP Server", "Connect external model context tools") {
            OutlinedTextField(
                value = endpoint,
                onValueChange = { endpoint = it; settingsState.mcpEndpoint = it },
                label = { Text("Endpoint") },
                modifier = Modifier.fillMaxWidth()
            )
            SettingRow("Enabled", endpoint.isNotBlank(), {
                settingsState.mcpEndpoint = if (it) endpoint else ""
            })
        }
        SettingCard("Modes", "Choose which AI specialists stay active") {
            modes.forEach { (title, enabled) ->
                SettingRow(title, enabled) { modes[title] = it; settingsState.updateMode(title, it) }
            }
        }
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

private fun SettingsState.updateMode(title: String, enabled: Boolean) {
    when (title) {
        "Architect" -> enableArchitect = enabled
        "Coder" -> enableCoder = enabled
        "Debugger" -> enableDebugger = enabled
    }
}
