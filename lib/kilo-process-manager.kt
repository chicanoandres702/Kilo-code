/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Implement native ProcessBuilder wrapper for Kilo CLI
 * [Upstream] KiloTermux -> [Downstream] Android Process API
 * [Law Check] 34 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import java.io.File
import java.util.concurrent.TimeUnit

class KiloProcessManager(private val context: Context) {
    private val binaryFile = File(context.filesDir, "kilo")

    fun runCommand(cmd: String, args: List<String> = emptyList()): CommandResult {
        return try {
            val process = ProcessBuilder(mutableListOf(binaryFile.absolutePath, cmd) + args)
                .directory(context.filesDir)
                .redirectErrorStream(true)
                .start()
            
            val stdout = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor(10, TimeUnit.SECONDS)
            
            CommandResult(stdout, "", process.exitValue())
        } catch (e: Exception) {
            CommandResult("", e.message ?: "Unknown error", 1)
        }
    }

    fun isBinaryInstalled(): Boolean = binaryFile.exists()
}