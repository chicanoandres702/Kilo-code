/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #21
 * [Subtask] Execute Android launcher through shell when noexec blocks direct runs
 * [Upstream] KiloTermux -> [Downstream] Android Process API
 * [Law Check] 69 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import java.io.File
import java.io.IOException

class KiloProcessManager(private val context: Context) {
    private val binaryFile = File(context.filesDir, "kilo")

    fun runCommand(cmd: String, args: List<String> = emptyList()): CommandResult {
        if (!isBinaryInstalled()) {
            return CommandResult("", "Kilo CLI is missing at ${binaryFile.absolutePath}. Retry setup in the app.", 1)
        }
        return try {
            val process = processBuilder(binaryFile, listOf(cmd) + args).start()
            val exitCode = process.waitFor()
            val stdout = process.inputStream.bufferedReader().use { it.readText() }
            CommandResult(stdout, "", exitCode)
        } catch (e: Exception) {
            CommandResult("", "Failed to run Kilo CLI at ${binaryFile.absolutePath}: ${e.message ?: "Unknown error"}", 1)
        }
    }

    fun startProcess(cmd: String, args: List<String> = emptyList()): Process {
        if (!isBinaryInstalled()) throw IOException("Kilo CLI is missing at ${binaryFile.absolutePath}")
        return processBuilder(binaryFile, listOf(cmd) + args).start()
    }

    fun verifyLauncher(binary: File): CommandResult = try {
        val process = processBuilder(binary, listOf("--version")).start()
        val exitCode = process.waitFor()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        CommandResult(output, "", exitCode)
    } catch (e: Exception) {
        CommandResult("", e.message ?: "Unable to verify Kilo launcher", 1)
    }

    private fun processBuilder(binary: File, args: List<String>): ProcessBuilder {
        val command = if (binary.isShellScript()) listOf("/system/bin/sh", binary.absolutePath) + args else listOf(binary.absolutePath) + args
        return ProcessBuilder(command).directory(context.filesDir).redirectErrorStream(true)
    }

    private fun File.isShellScript(): Boolean = isFile && canRead() && runCatching {
        inputStream().bufferedReader().use { it.readLine()?.startsWith("#!") == true }
    }.getOrDefault(false)

    fun isLauncherReady(hasNodeLauncher: Boolean): Boolean {
        val text = launcherText()
        return binaryFile.isShellScript() &&
            ((text.contains("@kilocode/cli") && hasNodeLauncher) || (text.contains("native-kilo") && isNativeBinaryInstalled()))
    }

    private fun launcherText(): String = if (binaryFile.isFile && binaryFile.canRead()) binaryFile.readText() else ""

    fun isNativeBinaryInstalled(): Boolean {
        val native = File(context.codeCacheDir ?: context.cacheDir, "kilo")
        return native.isFile && native.canRead() && native.canExecute() && native.length() > 0
    }

    fun isBinaryInstalled(): Boolean = binaryFile.isFile && binaryFile.canRead() && binaryFile.canExecute() && binaryFile.length() > 0
}
