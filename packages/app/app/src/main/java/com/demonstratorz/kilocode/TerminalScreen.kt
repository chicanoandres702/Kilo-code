/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #22
 * [Subtask] Complete terminal interaction in the Terminal section
 * [Upstream] MainScreen -> [Downstream] Android shell and Kilo CLI
 * [Law Check] 97 lines | Passed Do It Check
 */
package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.kilocli.android.KiloTermux
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.*

@Composable
fun TerminalScreen(kiloTermux: KiloTermux, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    var command by remember { mutableStateOf("") }
    var output by remember { mutableStateOf(listOf(TerminalLine.system("Terminal ready. Shell commands run in ${context.filesDir.absolutePath}."))) }
    var history by remember { mutableStateOf(emptyList<String>()) }
    var historyIndex by remember { mutableStateOf(-1) }
    var busy by remember { mutableStateOf(false) }
    fun append(line: String) { output = output + TerminalLine.output(line) }
    fun submit(rawCommand: String) {
        val snapshot = rawCommand.trim()
        if (snapshot.isBlank()) return
        command = ""
        historyIndex = -1
        history = history + snapshot
        append("$ $snapshot")
        scope.launch {
            busy = true
            try {
                val channel = Channel<String>(Channel.UNLIMITED)
                val job = launch {
                    for (line in channel) append(line)
                }
                val result = withContext(Dispatchers.IO) { runShell(context, snapshot) { channel.trySend(it) } }
                channel.close()
                job.join()
                append("exit ${result.exitCode}")
            } finally { busy = false }
        }
    }
    fun submitKilo(rawCommand: String) {
        val snapshot = rawCommand.trim()
        if (snapshot.isNotBlank()) submit("./kilo $snapshot")
    }
    fun recall(delta: Int) {
        if (history.isEmpty()) return
        val next = (historyIndex + delta).coerceIn(-1, history.lastIndex)
        historyIndex = next
        command = if (next < 0) "" else history[next]
    }
    fun installAutomatically() {
        scope.launch {
            busy = true
            try {
                append("$ ./kilo --version")
                val result = withContext(Dispatchers.IO) { kiloTermux.installNow() }
                append(result.text())
            } finally { busy = false }
        }
    }
    LaunchedEffect(output.size) { scrollState.animateScrollToItem(output.lastIndex) }
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Terminal", style = MaterialTheme.typography.headlineSmall)
            Text("A focused shell for Kilo setup, diagnostics, and direct CLI commands.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedTextField(value = command, onValueChange = { command = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Command") }, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { submit(command) }))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { submit(command) }, enabled = !busy) { Text("Run shell") }
            Button(onClick = { submitKilo(command) }, enabled = !busy) { Text("Run Kilo") }
            OutlinedButton(onClick = { command = manualInstallCommands(context.filesDir.absolutePath) }, enabled = !busy) { Text("Type install") }
            OutlinedButton(onClick = { installAutomatically() }, enabled = !busy) { Text("Run installer") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { recall(-1) }, enabled = !busy && history.isNotEmpty()) { Text("↑") }
            OutlinedButton(onClick = { recall(1) }, enabled = !busy && history.isNotEmpty()) { Text("↓") }
            OutlinedButton(onClick = { output = listOf(TerminalLine.system("Terminal cleared.")) }, enabled = !busy) { Text("Clear") }
        }
        TerminalOutput(output, scrollState)
        if (busy) CircularProgressIndicator(Modifier.padding(top = 8.dp))
    }
}
