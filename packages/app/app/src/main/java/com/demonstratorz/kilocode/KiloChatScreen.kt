/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Fix KiloChatScreen structure and command invocation
 * [Upstream] MainActivity -> [Downstream] KiloChatScreen
 * [Law Check] 60 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.KiloMode
import com.kilocli.android.KiloTermux
import com.kilocli.android.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiloChatScreen(kiloTermux: KiloTermux, settings: SettingsState, modifier: Modifier = Modifier) {
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var input by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(KiloMode.Coder) }
    var isAutonomous by remember { mutableStateOf(false) }

    fun send() {
        if (input.isBlank()) return
        messages = messages + Pair(input, true)
        messages = messages + Pair("Planning...", false)

        val plan = kiloTermux.runCommand("kilo", listOf("plan", input))
        messages = messages + Pair("Plan: ${plan.stdout}", false)

        val args = mutableListOf("run", "--mode", selectedMode.name.lowercase(), input)
        if (isAutonomous) args.add("--auto")
        if (settings.mcpEndpoint.isNotBlank()) {
            args.add("--mcp")
            args.add(settings.mcpEndpoint)
        }
        val result = kiloTermux.runCommand("kilo", args)
        messages = messages + Pair(result.stdout.ifBlank { result.stderr }, false)
        input = ""
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedMode == KiloMode.Architect, onClick = { selectedMode = KiloMode.Architect }, label = { Text("Architect") })
            FilterChip(selected = selectedMode == KiloMode.Coder, onClick = { selectedMode = KiloMode.Coder }, label = { Text("Coder") })
            FilterChip(selected = selectedMode == KiloMode.Debugger, onClick = { selectedMode = KiloMode.Debugger }, label = { Text("Debugger") })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAutonomous, onCheckedChange = { isAutonomous = it })
            Text("Autonomous")
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { (message, isUser) ->
                Text(message, color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
            Button(onClick = { send() }, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }
        }
    }
}
