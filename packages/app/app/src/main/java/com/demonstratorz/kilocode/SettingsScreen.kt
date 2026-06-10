/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Create hierarchical SettingsScreen tree
 * [Upstream] MainActivity -> [Downstream] SettingsScreen UI
 * [Law Check] 84 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.SettingsState

data class SettingNode(
    val title: String,
    var enabled: Boolean = true,
    val children: List<SettingNode> = emptyList()
)

@Composable
fun SettingsScreen(settingsState: SettingsState, modifier: Modifier = Modifier) {
    val expanded = remember { mutableStateMapOf("MCP" to true, "Modes" to true) }
    val nodes = remember(settingsState) {
        listOf(
            SettingNode("MCP", children = listOf(
                SettingNode("Server Endpoint", children = listOf(
                    SettingNode("Endpoint: ${settingsState.mcpEndpoint.ifBlank { "<empty>" }}")
                ))
            )),
            SettingNode("Modes", children = listOf(
                SettingNode("Architect", enabled = settingsState.enableArchitect),
                SettingNode("Coder", enabled = settingsState.enableCoder),
                SettingNode("Debugger", enabled = settingsState.enableDebugger)
            ))
        )
    }

    Column(modifier = modifier.padding(16.dp).fillMaxSize()) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        nodes.forEach { node ->
            SettingNodeRow(node, expanded) { title, checked -> updateSetting(settingsState, title, checked) }
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun SettingNodeRow(
    node: SettingNode,
    expanded: MutableStateMap<String, Boolean>,
    onToggle: (String, Boolean) -> Unit
) {
    val isExpanded = expanded[node.title] ?: false
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { expanded[node.title] = !isExpanded }) { Text(if (isExpanded) "v" else ">") }
        Checkbox(checked = node.enabled, onCheckedChange = { onToggle(node.title, it) })
        Text(node.title, style = MaterialTheme.typography.bodyLarge)
    }
    if (isExpanded && node.children.isNotEmpty()) {
        Column(modifier = Modifier.padding(start = 32.dp)) {
            node.children.forEach { child -> SettingNodeRow(child, expanded, onToggle) }
        }
    }
}

private fun updateSetting(settingsState: SettingsState, title: String, checked: Boolean) {
    when (title) {
        "Architect" -> settingsState.enableArchitect = checked
        "Coder" -> settingsState.enableCoder = checked
        "Debugger" -> settingsState.enableDebugger = checked
        "Modes" -> {
            settingsState.enableArchitect = checked
            settingsState.enableCoder = checked
            settingsState.enableDebugger = checked
        }
        "MCP" -> if (!checked) settingsState.mcpEndpoint = ""
    }
}
