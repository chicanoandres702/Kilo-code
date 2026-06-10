/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Compose theme for Material3
 * [Law Check] 24 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5), // Indigo
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = Color(0xFF0891B2), // Cyan
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF8FAFC), // Crisp Card
    onSecondaryContainer = Color(0xFF1E293B),
    background = Color(0xFFF1F5F9), // Soft Light Background
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF64748B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6366F1), // Electric Indigo
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF312E81), // Deep Indigo
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF06B6D4), // Cyan Glow
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF1E293B), // Slate Grey Card
    onSecondaryContainer = Color(0xFFF1F5F9),
    background = Color(0xFF090D16), // Dark Space Background
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111827), // Deep Surface
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF9CA3AF)
)

@Composable
fun KiloAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, content = content)
}