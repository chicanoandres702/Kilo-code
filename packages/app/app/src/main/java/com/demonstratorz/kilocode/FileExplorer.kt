/*
 * [Parent Feature/Milestone] Android Stability
 * [Child Task/Issue] #12
 * [Subtask] Create FileExplorer component
 * [Upstream] KiloTermux -> [Downstream] FileExplorer UI
 * [Law Check] 50 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilocli.android.KiloTermux

@Composable
fun FileExplorer(kiloTermux: KiloTermux, modifier: Modifier = Modifier) {
    var files by remember { mutableStateOf(listOf<String>()) }
    
    LaunchedEffect(Unit) {
        val result = kiloTermux.runCommand("ls", listOf("-1"))
        files = result.stdout.split("\n").filter { it.isNotBlank() }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Project Files", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(files) { file ->
                ListItem(headlineContent = { Text(file) })
            }
        }
    }
}
