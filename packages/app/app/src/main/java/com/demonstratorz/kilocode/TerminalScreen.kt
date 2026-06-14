/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Child Task/Issue] #12
 * [Subtask] Create TerminalScreen for terminal tab
 * [Upstream] MainActivity -> [Downstream] TerminalView
 * [Law Check] 45 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.termux.view.TerminalView
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

@Composable
fun TerminalScreen(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val terminalView = TerminalView(ctx, null)
            terminalView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Minimal session client implementation
            val sessionClient = object : TerminalSessionClient {
                override fun onTextChanged(changedSession: TerminalSession?) {}
                override fun onTitleChanged(changedSession: TerminalSession?) {}
                override fun onSessionFinished(finishedSession: TerminalSession?) {}
                override fun onCopyTextToClipboard(text: String?) {}
                override fun onPasteTextFromClipboard() {}
                override fun onBell(session: TerminalSession?) {}
                override fun onColorsChanged(session: TerminalSession?) {}
                override fun logInfo(tag: String?, message: String?) {}
                override fun logError(tag: String?, message: String?) {}
                override fun logWarn(tag: String?, message: String?) {}
            }

            val session = TerminalSession(
                "/system/bin/sh",
                ctx.filesDir.absolutePath,
                arrayOf(),
                arrayOf("TERM=xterm-256color", "HOME=${ctx.filesDir.absolutePath}"),
                1000,
                sessionClient
            )

            terminalView.attachSession(session)
            terminalView.setTextSize(30)
            terminalView
        }
    )
}
