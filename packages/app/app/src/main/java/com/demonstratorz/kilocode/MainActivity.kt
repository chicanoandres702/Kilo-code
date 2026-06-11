/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #21
 * [Subtask] Emit explicit Android install states
 * [Upstream] KiloTermux -> [Downstream] MainScreen
 * [Law Check] 43 lines | Passed Do It Check
 */

package com.demonstratorz.kilocode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.demonstratorz.kilocode.ui.theme.KiloAndroidTheme
import com.kilocli.android.InstallState
import com.kilocli.android.KiloTermux
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    private lateinit var kiloTermux: KiloTermux

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kiloTermux = KiloTermux.create(this)

        setContent {
            var installState by remember { mutableStateOf(InstallState(status = "Checking Kilo CLI...")) }
            LaunchedEffect(kiloTermux) {
                kiloTermux.initialize().collect { installState = it }
            }

            KiloAndroidTheme {
                MainScreen(kiloTermux, installState) {
                    installState = InstallState(status = "Checking Kilo CLI...")
                }
            }
        }
    }
}
