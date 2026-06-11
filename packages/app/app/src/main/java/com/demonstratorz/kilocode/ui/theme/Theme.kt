/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Child Task/Issue] #20
 * [Subtask] Apply dark GitHub-inspired neon blue theme
 * [Upstream] Android UI shell -> [Downstream] Compose MaterialTheme consumers
 * [Law Check] 75 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F81FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEAF2FF),
    onPrimaryContainer = Color(0xFF0969DA),
    secondary = Color(0xFF58A6FF),
    onSecondary = Color(0xFF0A1018),
    secondaryContainer = Color(0xFFEAF2FF),
    onSecondaryContainer = Color(0xFF0969DA),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF24292F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF24292F),
    surfaceVariant = Color(0xFFF6F8FA),
    onSurfaceVariant = Color(0xFF57606A),
    outline = Color(0xFFD0D7DE),
    outlineVariant = Color(0xFFD8DEE9),
    inverseSurface = Color(0xFF24292F),
    inverseOnSurface = Color(0xFFF6F8FA),
    inversePrimary = Color(0xFF2F81FF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF58A6FF),
    onPrimary = Color(0xFF0A1018),
    primaryContainer = Color(0xFF1F6FEB),
    onPrimaryContainer = Color(0xFFEAF2FF),
    secondary = Color(0xFF2F81FF),
    onSecondary = Color(0xFF0A1018),
    secondaryContainer = Color(0xFF1F6FEB),
    onSecondaryContainer = Color(0xFFEAF2FF),
    tertiary = Color(0xFF79C0FF),
    onTertiary = Color(0xFF0A1018),
    tertiaryContainer = Color(0xFF1F6FEB),
    onTertiaryContainer = Color(0xFFEAF2FF),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFC9D1D9),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFC9D1D9),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    outline = Color(0xFF30363D),
    outlineVariant = Color(0xFF30363D),
    inverseSurface = Color(0xFFF0F6FC),
    inverseOnSurface = Color(0xFF0D1117),
    inversePrimary = Color(0xFF2F81FF),
    surfaceContainer = Color(0xFF1C2128),
    surfaceContainerHigh = Color(0xFF30363D),
    surfaceContainerHighest = Color(0xFF30363D)
)

@Composable
fun KiloAndroidTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, content = content)
}