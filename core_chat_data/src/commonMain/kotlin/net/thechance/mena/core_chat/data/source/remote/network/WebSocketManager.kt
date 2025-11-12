package net.thechance.mena.core_chat.data.source.remote.network

import kotlinx.coroutines.flow.SharedFlow

interface WebSocketManager {
    val incomingMessages: SharedFlow<String>
    val connectionStatus: SharedFlow<Boolean>
    fun connect(onConnected: suspend () -> Unit)
    suspend fun subscribe(destination: String)
    suspend fun disconnect()
    suspend fun sendTextFrame(destination: String, payload: String)
    fun isConnected(): Boolean
}