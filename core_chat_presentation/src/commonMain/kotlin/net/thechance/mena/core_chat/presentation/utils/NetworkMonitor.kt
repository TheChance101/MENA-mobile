package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

expect class NetworkMonitor() {
    fun startMonitoring(onStatusChange: (NetworkStatus) -> Unit)
    fun stopMonitoring()
}

@Composable
fun rememberNetworkStatus(): State<Boolean> {
    val networkStatusState = remember { mutableStateOf(true) }
    val networkMonitor = remember { NetworkMonitor() }

    DisposableEffect(Unit) {
        networkMonitor.startMonitoring { status ->
            networkStatusState.value = when (status) {
                NetworkStatus.CONNECTED -> true
                NetworkStatus.DISCONNECTED -> false
            }
        }

        onDispose {
            networkMonitor.stopMonitoring()
        }
    }

    return networkStatusState
}