/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Define models for Kilo Termux integration
 * [Law Check] 40 lines
 */

package com.kilocli.android

// Configuration
data class KiloTermuxConfig(
    val autoInstallBootstrap: Boolean = true,
    val logLevel: String = "INFO",
    val serverPort: Int = 0,
    val apiEndpoint: String = "",
    val mcpEndpoint: String = ""
)

// UI Settings State
data class SettingsState(
    var mcpEndpoint: String = "",
    var enableArchitect: Boolean = true,
    var enableCoder: Boolean = true,
    var enableDebugger: Boolean = true
)

// Server state
data class KiloServerState(
    val status: ServerStatus = ServerStatus.Stopped,
    val port: Int? = null,
    val pid: Int? = null,
    val error: String? = null
)

enum class ServerStatus { Starting, Running, Stopped, Error }

// Command result
data class CommandResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)

// Mode selection
enum class KiloMode { Architect, Coder, Debugger }

// Install state
data class InstallState(
    val progress: Float = 0f,
    val status: String = "",
    val isComplete: Boolean = false
)

// Output line sealed class
sealed class OutputLine {
    data class Stdout(val text: String) : OutputLine()
    data class Stderr(val text: String) : OutputLine()
    data class Exit(val code: Int) : OutputLine()
}