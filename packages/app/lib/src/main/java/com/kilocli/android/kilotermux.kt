/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #21
 * [Subtask] Add npm-backed Kilo CLI auto-install launcher
 * [Upstream] MainActivity -> [Downstream] KiloProcessManager
 * [Law Check] 91 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import android.system.Os
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.IOException

class KiloTermux(private val context: Context) {
    private val processManager = KiloProcessManager(context)
    private val installLock = Any()

    fun initialize(): Flow<InstallState> = callbackFlow {
        val installError = ensureBinaryInstalled { trySend(it) }
        trySend(InstallState(progress = if (installError == null) 1f else 0.95f, status = installError ?: "Ready", isComplete = installError == null))
        close()
    }

    private fun ensureBinaryInstalled(onStatus: ((InstallState) -> Unit)? = null): String? = synchronized(installLock) {
        if (!processManager.isBinaryInstalled()) return@synchronized try {
            onStatus?.invoke(InstallState(progress = 0.2f, status = "Installing Kilo CLI with npm..."))
            installKiloBinary(onStatus)
            null
        } catch (e: Exception) {
            e.message ?: "Unable to install Kilo CLI"
        }
        null
    }

    private fun installKiloBinary(onStatus: ((InstallState) -> Unit)?) {
        val binary = File(context.filesDir, "kilo")
        val tempBinary = File(context.filesDir, "kilo.tmp")
        val backupBinary = File(context.filesDir, "kilo.backup")
        tempBinary.delete()
        backupBinary.delete()
        onStatus?.invoke(InstallState(progress = 0.45f, status = "Preparing npm launcher..."))
        tempBinary.writeText(launcherScript())
        if (!tempBinary.setReadable(true, false) || !tempBinary.setExecutable(true, false)) {
            throw IOException("Unable to set executable permissions on Kilo launcher: ${tempBinary.absolutePath}")
        }
        try { Os.chmod(tempBinary.absolutePath, 0x1C0) } catch (_: Exception) {}
        if (binary.exists() && !binary.renameTo(backupBinary)) {
            throw IOException("Unable to replace existing Kilo launcher: ${binary.absolutePath}")
        }
        if (!tempBinary.renameTo(binary)) {
            backupBinary.renameTo(binary)
            throw IOException("Unable to install Kilo launcher at ${binary.absolutePath}")
        }
        onStatus?.invoke(InstallState(progress = 0.8f, status = "Verifying Kilo CLI launcher..."))
        verifyLauncher(binary)
        if (!processManager.isBinaryInstalled()) throw IOException("Installed Kilo launcher is not executable: ${binary.absolutePath}")
    }

    private fun launcherScript(): String = "#!/system/bin/sh\nset -e\nif command -v npx >/dev/null 2>&1; then\n  exec npx --yes --package @kilocode/cli kilo \"\$@\"\nfi\nif command -v npm >/dev/null 2>&1; then\n  exec npm exec --yes --package @kilocode/cli -- kilo \"\$@\"\nfi\necho \"Missing npm/npx. Install Node.js or Termux npm first.\" >&2\nexit 127\n"

    private fun verifyLauncher(binary: File) {
        val process = ProcessBuilder(binary.absolutePath, "--version").directory(context.filesDir).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()
        if (exitCode != 0) throw IOException("Kilo launcher verification failed: ${output.ifBlank { "exit code $exitCode" }}")
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
