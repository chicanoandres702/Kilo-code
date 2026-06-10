/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Compose theme for Material3
 * [Law Check] 24 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD9E6F2),
    onPrimaryContainer = Color(0xFF1A3A5F)
)

@Composable
fun KiloAndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}