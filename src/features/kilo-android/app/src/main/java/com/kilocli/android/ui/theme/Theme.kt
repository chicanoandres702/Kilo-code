/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Compose theme for Material3
 * [Law Check] 30 lines
 */

package com.kilocli.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF4A90E2),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD9E6F2),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1A3A5F)
)

@Composable
fun KiloAndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}