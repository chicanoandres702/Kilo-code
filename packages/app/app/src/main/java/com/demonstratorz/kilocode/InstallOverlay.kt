/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Render install loader and retry overlay
 * [Upstream] KiloTermux InstallState -> [Downstream] MainScreen
 * [Law Check] 74 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kilocli.android.InstallState

@Composable
fun InstallOverlay(installState: InstallState, onRetry: () -> Unit) {
    val failed = installState.isComplete && installState.status != "Ready"
    val message = installState.status.ifBlank { "Installing Kilo runtime..." }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                if (failed) {
                    Text("Kilo setup failed", style = MaterialTheme.typography.titleMedium)
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = installState.progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                if (failed) {
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) { Text("Retry") }
                }
            }
        }
    }
}
