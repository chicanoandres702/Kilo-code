/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Fix FileExplorer integration and KiloChatScreen structure
 * [Upstream] MainActivity -> [Downstream] KiloChatScreen
 * [Law Check] 75 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

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
import com.kilocli.android.KiloMode
import com.kilocli.android.KiloTermux
import com.kilocli.android.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiloChatScreen(kiloTermux: KiloTermux, settings: SettingsState, modifier: Modifier = Modifier) {
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var input by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(KiloMode.Coder) }
    var isAutonomous by remember { mutableStateOf(false) }

    // ...
    
    // In the "Send" button onClick:
    Button(onClick = {
        messages = messages + Pair(input, true)
        
        // Planning Step
        messages = messages + Pair("Planning...", false)
        val plan = kiloTermux.runCommand("kilo", listOf("plan", input))
        messages = messages + Pair("Plan: ${plan.stdout}", false)
        
        // Execution Step
        val args = mutableListOf("run", "--mode", selectedMode.name.lowercase(), input)
        if (isAutonomous) args.add("--auto")
        if (settings.mcpEndpoint.isNotEmpty()) {
            args.add("--mcp")
            args.add(settings.mcpEndpoint)
        }
        val result = kiloTermux.runCommand("kilo", args)
        messages = messages + Pair(result.stdout, false)
        input = ""
    }, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }

            }
        }
    }
}
