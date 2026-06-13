/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Auto-install bundled Kilo binary before command execution
 * [Upstream] MainActivity -> [Downstream] LibTermux environment
 * [Law Check] 79 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class KiloTermux(private val context: Context) {
    private val processManager = KiloProcessManager(context)
    private val installLock = Any()

    fun initialize(): Flow<InstallState> = callbackFlow {
        trySend(InstallState(progress = 0.5f, status = "Checking Kilo binary..."))

        val installError = ensureBinaryInstalled()
        if (installError != null) {
            trySend(InstallState(progress = 1.0f, status = installError))
        } else {
            trySend(InstallState(progress = 1.0f, status = "Ready", isComplete = true))
        }

        close()
    }

    private fun ensureBinaryInstalled(): String? {
        var installError: String? = null
        synchronized(installLock) {
            if (!processManager.isBinaryInstalled()) {
                try {
                    installKiloBinary()
                } catch (e: Exception) {
                    installError = e.message ?: "Unable to install Kilo binary"
                }
            }
        }
        return installError
    }

    private fun installKiloBinary() {
        val assetName = resolveAssetName()
        // Attempt installation in filesDir first (default)
        val binary = File(context.filesDir, "kilo")

        try {
            installTo(assetName, binary)
        } catch (e: IOException) {
            // Fallback to cacheDir if filesDir fails
            val fallback = File(context.cacheDir, "kilo")
            installTo(assetName, fallback)
        }
    }

    private fun installTo(assetName: String, destination: File) {
        context.assets.open(assetName).use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }

            // Explicitly set executable
            if (!destination.setExecutable(true, false)) {
                throw IOException("Unable to set executable permissions: ${destination.absolutePath}. Filesystem might be noexec.")
            }

            if (!destination.canExecute()) {
                throw IOException("Binary not executable after chmod: ${destination.absolutePath}")
            }
        }
    }

    private fun resolveAssetName(): String {
        val assetNames = context.assets.list("")?.toSet().orEmpty()
        val candidates = Build.SUPPORTED_ABIS.map { "kilo-linux-$it" } +
            listOf("kilo-linux-arm64", "kilo-linux-x64", "kilo")

        return candidates.firstOrNull { it in assetNames }
            ?: throw FileNotFoundException("Missing Kilo binary asset. Expected one of: ${candidates.joinToString()}")
    }

    fun runCommand(cmd: String, args: List<String> = emptyList()): CommandResult {
        val installError = ensureBinaryInstalled()
        if (installError != null) {
            return CommandResult("", installError, 1)
        }
        return processManager.runCommand(cmd, args)
    }

    fun runServer(): Flow<String> = callbackFlow {
        trySend("Starting server...")
        val result = processManager.runCommand("server", listOf("start"))
        trySend(result.stdout)
        if (result.stderr.isNotEmpty()) {
            trySend("Error: ${result.stderr}")
        }
        close()
    }

    companion object {
        fun create(context: Context) = KiloTermux(context)
    }
}
