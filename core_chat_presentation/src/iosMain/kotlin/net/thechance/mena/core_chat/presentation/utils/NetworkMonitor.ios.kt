package net.thechance.mena.core_chat.presentation.utils

import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_monitor_t
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

actual class NetworkMonitor actual constructor() {
    private var monitor: nw_path_monitor_t? = null

    actual fun startMonitoring(onStatusChange: (NetworkStatus) -> Unit) {

        monitor = nw_path_monitor_create().also { pathMonitor ->
            nw_path_monitor_set_update_handler(pathMonitor) { path ->
                val isConnected = nw_path_get_status(path) == nw_path_status_satisfied
                dispatch_async(dispatch_get_main_queue()) {
                    onStatusChange(
                        if (isConnected) NetworkStatus.CONNECTED else NetworkStatus.DISCONNECTED
                    )
                }
            }

            val queue = dispatch_queue_create("NetworkMonitor", null)
            nw_path_monitor_set_queue(pathMonitor, queue)
            nw_path_monitor_start(pathMonitor)
        }
    }

    actual fun stopMonitoring() {
        monitor?.let { nw_path_monitor_cancel(it) }
        monitor = null
    }
}