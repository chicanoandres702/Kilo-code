/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Create polished FileExplorer component
 * [Upstream] KiloTermux -> [Downstream] FileExplorer UI
 * [Law Check] 88 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.KiloTermux

@Composable
fun FileExplorer(kiloTermux: KiloTermux, modifier: Modifier = Modifier) {
    var files by remember { mutableStateOf(listOf<String>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = kiloTermux.runCommand("ls", listOf("-1"))
        files = result.stdout.split("\n").filter { it.isNotBlank() }
        loading = false
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Project Files", style = MaterialTheme.typography.headlineSmall)
        Text("Browse your workspace", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 3.dp
        ) {
            LazyColumn {
                if (loading) item { FileRow("Loading files...", Icons.Default.Folder) }
                items(files) { file -> FileRow(file, fileIcon(file)) }
            }
        }
    }
}

@Composable
private fun FileRow(file: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(icon, file, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(file, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Open", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun fileIcon(file: String) = when {
    file.endsWith(".kt") || file.endsWith(".java") -> Icons.Default.Code
    file.endsWith(".png") || file.endsWith(".jpg") -> Icons.Default.Image
    file.endsWith(".md") -> Icons.Default.Description
    file.contains("/") -> Icons.Default.Folder
    else -> Icons.Default.InsertDriveFile
}
