/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #22
 * [Subtask] Complete terminal interaction in the Terminal section
 * [Upstream] TerminalScreen -> [Downstream] Android shell and Kilo CLI
 * [Law Check] 77 lines | Passed Do It Check
 */
package com.demonstratorz.kilocode

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.CommandResult

@Composable
fun TerminalOutput(lines: List<TerminalLine>, scrollState: LazyListState) {
    Surface(modifier = Modifier.weight(1f).fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
        LazyColumn(Modifier.padding(12.dp), state = scrollState) {
            itemsIndexed(lines) { _, line -> TerminalLineView(line) }
        }
    }
}

@Composable
private fun TerminalLineView(line: TerminalLine) {
    val color = when (line.kind) {
        TerminalLine.Kind.Output -> MaterialTheme.colorScheme.onSurfaceVariant
        TerminalLine.Kind.System -> MaterialTheme.colorScheme.primary
    }
    Text(line.text, style = MaterialTheme.typography.bodyMedium, color = color)
}

sealed class TerminalLine(val text: String, val kind: Kind) {
    enum class Kind { Output, System }
    class Output(text: String) : TerminalLine(text, Kind.Output)
    class System(text: String) : TerminalLine(text, Kind.System)
    companion object {
        fun output(text: String) = Output(text)
        fun system(text: String) = System(text)
    }
}

fun manualInstallCommands(filesDir: String) = """
cd "$filesDir"
if [ ! -x ./kilo ]; then
  curl -fL -o kilo-linux-arm64-musl.tar.gz https://github.com/Kilo-Org/kilocode/releases/latest/download/kilo-linux-arm64-musl.tar.gz
  tar -xzf kilo-linux-arm64-musl.tar.gz kilo
  chmod +x kilo
fi
./kilo --version
""".trimIndent()

fun runShell(context: Context, command: String, onOutput: (String) -> Unit = {}): CommandResult = runCatching {
    val process = ProcessBuilder("/system/bin/sh", "-lc", command)
        .directory(context.filesDir)
        .redirectErrorStream(true)
        .start()
    val stdout = buildString {
        process.inputStream.bufferedReader().use { reader ->
            reader.forEachLine { line ->
                appendLine(line)
                onOutput(line)
            }
        }
    }
    CommandResult(stdout, "", process.waitFor())
}.getOrElse { CommandResult("", it.message ?: "Unable to run shell command", 1) }

fun CommandResult.text(): String = listOfNotNull(stdout.trim().takeIf(String::isNotBlank), stderr.trim().takeIf(String::isNotBlank), "exit $exitCode").joinToString("\n").ifBlank { "exit $exitCode" }
