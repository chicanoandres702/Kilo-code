/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Core KiloTermux initialization and binary management
 * [Upstream] MainActivity -> [Downstream] LibTermux environment
 * [Law Check] 55 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

class KiloTermux(private val context: Context) {
    private val processManager = KiloProcessManager(context)

    fun initialize(): Flow<InstallState> = callbackFlow {
        trySend(InstallState(progress = 0.5f, status = "Checking Kilo binary..."))
        
        if (!processManager.isBinaryInstalled()) {
            trySend(InstallState(progress = 0.8f, status = "Extracting binary..."))
            installKiloBinary()
        }
        
        trySend(InstallState(progress = 1.0f, status = "Ready", isComplete = true))
        close()
    }

    private fun installKiloBinary() {
        context.assets.open("kilo").use { input ->
            val binary = File(context.filesDir, "kilo")
            binary.writeBytes(input.readBytes())
            binary.setExecutable(true)
        }
    }

    fun runCommand(cmd: String): CommandResult {
        return processManager.runCommand(cmd)
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