package net.thechance.mena.core_chat.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import org.koin.core.context.GlobalContext

actual class NetworkMonitor actual constructor() {
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var statusChangeListener: ((NetworkStatus) -> Unit)? = null

    actual fun startMonitoring(onStatusChange: (NetworkStatus) -> Unit) {
        val context = GlobalContext.get().get<Context>()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        statusChangeListener = onStatusChange

        val activeNetwork = connectivityManager?.activeNetwork
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isConnected = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        onStatusChange(if (isConnected) NetworkStatus.CONNECTED else NetworkStatus.DISCONNECTED)

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                statusChangeListener?.invoke(NetworkStatus.CONNECTED)
            }

            override fun onLost(network: Network) {
                statusChangeListener?.invoke(NetworkStatus.DISCONNECTED)
            }

            override fun onUnavailable() {
                statusChangeListener?.invoke(NetworkStatus.DISCONNECTED)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback?.let { callback ->
            connectivityManager?.registerNetworkCallback(request, callback)
        }
    }

    actual fun stopMonitoring() {
        networkCallback?.let { callback ->
            connectivityManager?.unregisterNetworkCallback(callback)
        }
        networkCallback = null
        connectivityManager = null
        statusChangeListener = null
    }
}