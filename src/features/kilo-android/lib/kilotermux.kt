/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Core KiloTermux initialization and binary management
 * [Upstream] MainActivity -> [Downstream] LibTermux environment
 * [Law Check] 85 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import com.libtermux.LibTermux
import com.libtermux.TermuxBridge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

class KiloTermux(private val context: Context) {
    private var termux: LibTermux? = null
    private var bridge: TermuxBridge? = null

    fun initialize(): Flow<InstallState> = callbackFlow {
        trySend(InstallState(progress = 0.1f, status = "Initializing..."))
        termux = LibTermux.create(context) {
            autoInstall = true
            logLevel = LogLevel.DEBUG
        }
        trySend(InstallState(progress = 0.5f, status = "Installing packages..."))
        installDependencies()
        trySend(InstallState(progress = 1.0f, status = "Ready", isComplete = true))
        close()
    }

    private suspend fun installDependencies() {
        bridge = termux?.bridge ?: return
        bridge?.run("pkg install -y nodejs bash nodejs")
        installKiloBinary()
    }

    private suspend fun installKiloBinary() {
        bridge?.run("mkdir -p ~/bin")
        context.assets.open("kilo").use { input ->
            val binary = File(context.filesDir, "kilo")
            binary.writeBytes(input.readBytes())
            binary.setExecutable(true)
            bridge?.run("cp ${binary.absolutePath} ~/bin/kilo")
        }
    }

    fun runServer(port: Int = 0): Flow<OutputLine> = callbackFlow {
        bridge?.run("pkill kilo || true")
        val cmd = "~/bin/kilo serve${if (port > 0) " --port $port" else ""}"
        bridge?.runStreaming(cmd)?.collect { line -> trySend(line) }
        close()
    }

    fun runCommand(cmd: String): CommandResult {
        val result = bridge?.run(cmd) ?: return CommandResult("", "Not initialized", 1)
        return CommandResult(result.stdout ?: "", result.stderr ?: "", result.exitCode ?: 1)
    }

    companion object {
        fun create(context: Context) = KiloTermux(context)
    }
}