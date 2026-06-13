/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Guard native ProcessBuilder against missing Kilo binary
 * [Upstream] KiloTermux -> [Downstream] Android Process API
 * [Law Check] 48 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import java.io.File
import java.io.IOException

class KiloProcessManager(private val context: Context) {
    private fun getBinaryFile(): File {
        val filesDirBinary = File(context.filesDir, "kilo")
        if (filesDirBinary.exists()) return filesDirBinary
        return File(context.cacheDir, "kilo")
    }

    fun runCommand(cmd: String, args: List<String> = emptyList()): CommandResult {
        val binaryFile = getBinaryFile()
        if (!isBinaryInstalled()) {
            return CommandResult("", "Kilo binary is missing at ${binaryFile.absolutePath}. Initialize KiloTermux before running commands.", 1)
        }

        return try {
            val process = ProcessBuilder(mutableListOf(binaryFile.absolutePath, cmd) + args)
                .directory(context.filesDir)
                .redirectErrorStream(true)
                .start()

            val exitCode = process.waitFor()
            val stdout = process.inputStream.bufferedReader().use { it.readText() }
            CommandResult(stdout, "", exitCode)
        } catch (e: Exception) {
            CommandResult("", "Failed to run Kilo binary at ${binaryFile.absolutePath}: ${e.message ?: "Unknown error"}", 1)
        }
    }

    // New method for long-running processes (like servers)
    fun startProcess(cmd: String, args: List<String> = emptyList()): Process {
        val binaryFile = getBinaryFile()
        if (!isBinaryInstalled()) {
            throw IOException("Kilo binary is missing at ${binaryFile.absolutePath}")
        }

        return ProcessBuilder(mutableListOf(binaryFile.absolutePath, cmd) + args)
            .directory(context.filesDir)
            .redirectErrorStream(true)
            .start()
    }

    fun isBinaryInstalled(): Boolean {
        val binaryFile = getBinaryFile()
        return binaryFile.isFile && binaryFile.canRead() && binaryFile.canExecute() && binaryFile.length() > 0
    }
}
