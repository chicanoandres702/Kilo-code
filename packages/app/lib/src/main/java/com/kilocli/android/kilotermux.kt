/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #21
 * [Subtask] Store Kilo CLI outside APK and chmod it
 * [Upstream] KiloTermux -> [Downstream] KiloProcessManager
 * [Law Check] 100 lines | Passed Do It Check
 */
package com.kilocli.android
import android.content.Context
import android.system.Os
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.IOException
class KiloTermux(private val context: Context) {
    private val processManager = KiloProcessManager(context)
    private val binary = KiloProcessManager.launcherFile(context)
    private val tempBinary = File(binary.parentFile, "kilo.tmp")
    private val backupBinary = File(binary.parentFile, "kilo.backup")
    private val nativeBinary = KiloProcessManager.nativeFile(context)
    private val tempNativeBinary = File(nativeBinary.parentFile, "kilo.tmp")
    private val installLock = Any()
    fun initialize(): Flow<InstallState> = callbackFlow {
        val installError = ensureBinaryInstalled { trySend(it) }
        trySend(InstallState(progress = if (installError == null) 1f else 0.95f, status = installError ?: "Ready", isComplete = installError == null))
        close()
    }.flowOn(Dispatchers.IO)
    fun installNow(): CommandResult {
        val error = ensureBinaryInstalled()
        return if (error == null) CommandResult("Kilo CLI is ready", "", 0) else CommandResult("", error, 1)
    }
    private fun ensureBinaryInstalled(onStatus: ((InstallState) -> Unit)? = null): String? = synchronized(installLock) {
        val hasNodeLauncher = hasNodeLauncher()
        if (!processManager.isBinaryInstalled() || !processManager.isLauncherReady(hasNodeLauncher)) return@synchronized try {
            onStatus?.invoke(InstallState(progress = 0.2f, status = "Installing Kilo CLI..."))
            installKiloBinary(hasNodeLauncher, onStatus)
            null
        } catch (e: Exception) {
            "Unable to install Kilo CLI: ${e::class.java.simpleName}: ${e.message ?: "no message"}"
        }
        null
    }
    private fun installKiloBinary(hasNodeLauncher: Boolean, onStatus: ((InstallState) -> Unit)?) {
        listOf(tempBinary, backupBinary, nativeBinary, tempNativeBinary).forEach { it.delete() }
        onStatus?.invoke(InstallState(progress = 0.45f, status = "Preparing launcher..."))
        if (hasNodeLauncher) {
            tempBinary.writeText(npmLauncherScript())
        } else {
            KiloReleaseInstaller(context).install(tempNativeBinary) { onStatus?.invoke(InstallState(progress = 0.65f, status = it)) }
            if (!tempNativeBinary.setReadable(true, false) || !tempNativeBinary.setExecutable(true, false)) throw IOException("Unable to set executable permissions on Kilo binary: ${tempNativeBinary.absolutePath}")
            try { Os.chmod(tempNativeBinary.absolutePath, 0x1C0) } catch (_: Exception) {}
            if (!tempNativeBinary.renameTo(nativeBinary)) throw IOException("Unable to install Kilo binary at ${nativeBinary.absolutePath}")
            tempBinary.writeText(nativeLauncherScript(nativeBinary.absolutePath))
        }
        if (!tempBinary.setReadable(true, false) || !tempBinary.setExecutable(true, false)) throw IOException("Unable to set executable permissions on Kilo launcher: ${tempBinary.absolutePath}")
        try { Os.chmod(tempBinary.absolutePath, 0x1C0) } catch (_: Exception) {}
        if (binary.exists() && !binary.renameTo(backupBinary)) throw IOException("Unable to replace existing Kilo launcher: ${binary.absolutePath}")
        if (!tempBinary.renameTo(binary)) {
            if (backupBinary.exists() && !backupBinary.renameTo(binary)) throw IOException("Unable to restore previous Kilo launcher: ${binary.absolutePath}")
            throw IOException("Unable to install Kilo launcher at ${binary.absolutePath}")
        }
        onStatus?.invoke(InstallState(progress = 0.8f, status = "Verifying Kilo CLI launcher..."))
        verifyLauncher(binary)
        if (!processManager.isBinaryInstalled()) throw IOException("Installed Kilo launcher is not executable: ${binary.absolutePath}")
    }
    private fun npmLauncherScript(): String = "#!/system/bin/sh\nset -e\nif command -v npx >/dev/null 2>&1; then\n  exec npx --yes --package @kilocode/cli kilo \"\$@\"\nfi\nif command -v npm >/dev/null 2>&1; then\n  exec npm exec --yes --package @kilocode/cli -- kilo \"\$@\"\nfi\necho \"Missing npm/npx. Install Node.js or Termux npm first.\" >&2\nexit 127\n"

    private fun nativeLauncherScript(path: String): String = "#!/system/bin/sh\n# native-kilo\nexec \"$path\" \"\$@\"\n"

    private fun hasNodeLauncher(): Boolean = hasCommand("npx") || hasCommand("npm")

    private fun hasCommand(command: String): Boolean = ProcessBuilder("/system/bin/sh", "-lc", "command -v $command >/dev/null 2>&1")
        .redirectErrorStream(true)
        .start()
        .waitFor() == 0
    private fun verifyLauncher(binary: File) {
        val result = processManager.verifyLauncher(binary)
        if (result.exitCode != 0) throw IOException("Kilo launcher verification failed: ${result.stdout.ifBlank { result.stderr }}")
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
    }.flowOn(Dispatchers.IO)

    companion object { fun create(context: Context) = KiloTermux(context) }
}
