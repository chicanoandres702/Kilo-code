/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #22
 * [Subtask] Add terminal access and typed install commands
 * [Upstream] MainScreen -> [Downstream] KiloTermux and Android shell
 * [Law Check] 100 lines | Passed Do It Check
 */
package com.demonstratorz.kilocode
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kilocli.android.CommandResult
import com.kilocli.android.KiloTermux
import kotlinx.coroutines.launch
@Composable
fun TerminalScreen(kiloTermux: KiloTermux, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var command by remember { mutableStateOf(manualInstallCommands(context.filesDir.absolutePath)) }
    var output by remember { mutableStateOf(listOf("Terminal ready. Press Run command or Type install commands.")) }
    var busy by remember { mutableStateOf(false) }
    fun append(line: String) { output = output + line }
    fun run(command: String) {
        scope.launch {
            busy = true
            append("$ $command")
            val result = runShell(context, command)
            append(result.text())
            busy = false
        }
    }
    fun installAutomatically() {
        scope.launch {
            busy = true
            append("$ kiloTermux.installNow()")
            val result = kiloTermux.installNow()
            append(result.text())
            busy = false
        }
    }
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Terminal", style = MaterialTheme.typography.headlineSmall)
        Text("Run shell commands or type the manual Kilo install sequence.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(value = command, onValueChange = { command = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Command") })
        Row {
            Button(onClick = { run(command) }, enabled = !busy) { Text("Run command") }
            OutlinedButton(onClick = { command = manualInstallCommands(context.filesDir.absolutePath) }, enabled = !busy) { Text("Type install commands") }
            OutlinedButton(onClick = { installAutomatically() }, enabled = !busy) { Text("Run installer") }
        }
        Surface(modifier = Modifier.weight(1f).fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
            LazyColumn(Modifier.padding(12.dp)) { items(output) { Text(it, style = MaterialTheme.typography.bodyMedium) } }
        }
        if (busy) CircularProgressIndicator(Modifier.padding(top = 8.dp))
    }
}
private fun manualInstallCommands(filesDir: String) = """
cd "$filesDir"
if [ ! -x ./kilo ]; then
  curl -fL -o kilo-linux-arm64-musl.tar.gz https://github.com/Kilo-Org/kilocode/releases/latest/download/kilo-linux-arm64-musl.tar.gz
  tar -xzf kilo-linux-arm64-musl.tar.gz kilo
  chmod +x kilo
fi
./kilo --version
""".trimIndent()
private fun runShell(context: Context, command: String): CommandResult = runCatching {
    val process = ProcessBuilder("/system/bin/sh", "-lc", command)
        .directory(context.filesDir)
        .redirectErrorStream(true)
        .start()
    val stdout = process.inputStream.bufferedReader().use { it.readText() }
    CommandResult(stdout, "", process.waitFor())
}.getOrElse { CommandResult("", it.message ?: "Unable to run shell command", 1) }
private fun CommandResult.text(): String = buildString {
    if (stdout.isNotBlank()) appendLine(stdout.trim())
    if (stderr.isNotBlank()) appendLine(stderr.trim())
    appendLine("exit $exitCode")
}.ifBlank { "exit $exitCode" }
