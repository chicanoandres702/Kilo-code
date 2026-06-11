/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Auto-install bundled Kilo binary before command execution
 * [Upstream] MainActivity -> [Downstream] LibTermux environment
 * [Law Check] 82 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import android.os.Build
import android.system.Os
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
        trySend(InstallState(progress = 1.0f, status = installError ?: "Ready", isComplete = installError == null))
        close()
    }

    private fun ensureBinaryInstalled(): String? = synchronized(installLock) {
        if (!processManager.isBinaryInstalled()) {
            return@synchronized try {
                installKiloBinary()
                null
            } catch (e: Exception) {
                e.message ?: "Unable to install Kilo binary"
            }
        }
        null
    }

    private fun installKiloBinary() {
        val binary = File(context.filesDir, "kilo")
        val tempBinary = File(context.filesDir, "kilo.tmp")
        val backupBinary = File(context.filesDir, "kilo.backup")
        tempBinary.delete()
        backupBinary.delete()
        context.assets.open(resolveAssetName()).use { input -> tempBinary.outputStream().use { output -> input.copyTo(output) } }

        if (!tempBinary.setReadable(true, false) || !tempBinary.setExecutable(true, false)) {
            throw IOException("Unable to set executable permissions on Kilo binary: ${tempBinary.absolutePath}")
        }

        try { Os.chmod(tempBinary.absolutePath, 0x1C0) } catch (_: Exception) {}

        if (binary.exists() && !binary.renameTo(backupBinary)) {
            throw IOException("Unable to replace existing Kilo binary: ${binary.absolutePath}")
        }

        if (!tempBinary.renameTo(binary)) {
            backupBinary.renameTo(binary)
            throw IOException("Unable to install Kilo binary at ${binary.absolutePath}")
        }

        if (!processManager.isBinaryInstalled()) {
            throw IOException("Installed Kilo binary is not executable: ${binary.absolutePath}")
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
        if (installError != null) return CommandResult("", installError, 1)
        return processManager.runCommand(cmd, args)
    }

    fun runServer(): Flow<String> = callbackFlow {
        trySend("Starting server...")
        val installError = ensureBinaryInstalled()
        if (installError != null) { trySend("Error: $installError"); close(); return@callbackFlow }
        val result = processManager.runCommand("server", listOf("start"))
        trySend(result.stdout)
        if (result.stderr.isNotEmpty()) trySend("Error: ${result.stderr}")
        close()
    }

    companion object {
        fun create(context: Context) = KiloTermux(context)
    }
}
