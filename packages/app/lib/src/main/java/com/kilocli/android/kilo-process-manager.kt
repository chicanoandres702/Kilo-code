/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Implement native ProcessBuilder wrapper for Kilo CLI
 * [Upstream] KiloTermux -> [Downstream] Android Process API
 * [Law Check] 34 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import java.io.File

class KiloProcessManager(private val context: Context) {
    private val binaryFile = File(context.filesDir, "kilo")

    fun runCommand(cmd: String, args: List<String> = emptyList()): CommandResult {
        return try {
            val process = ProcessBuilder(mutableListOf(binaryFile.absolutePath, cmd) + args)
                .directory(context.filesDir)
                .redirectErrorStream(true)
                .start()
            
            val exitCode = process.waitFor()
            val stdout = process.inputStream.bufferedReader().use { it.readText() }
            CommandResult(stdout, "", exitCode)
        } catch (e: Exception) {
            CommandResult("", e.message ?: "Unknown error", 1)
        }
    }

    // New method for long-running processes (like servers)
    fun startProcess(cmd: String, args: List<String> = emptyList()): Process {
        return ProcessBuilder(mutableListOf(binaryFile.absolutePath, cmd) + args)
            .directory(context.filesDir)
            .redirectErrorStream(true)
            .start()
    }

    fun isBinaryInstalled(): Boolean = binaryFile.exists()
}