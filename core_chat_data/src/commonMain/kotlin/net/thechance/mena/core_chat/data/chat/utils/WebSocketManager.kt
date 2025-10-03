package net.thechance.mena.core_chat.data.chat.utils

import kotlinx.coroutines.flow.SharedFlow

interface WebSocketManager {
    val incomingMessages: SharedFlow<String>
    fun connect(
        token: String,
        onConnected: suspend () -> Unit
    )
    suspend fun subscribe(destination: String)
    suspend fun disconnect()
    suspend fun sendTextFrame(destination: String, payload: String)
    fun isConnected(): Boolean
}
