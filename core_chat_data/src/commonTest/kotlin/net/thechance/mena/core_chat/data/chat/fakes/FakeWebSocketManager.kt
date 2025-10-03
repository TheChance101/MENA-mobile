/*
package net.thechance.mena.core_chat.data.chat.fakes

import io.ktor.client.HttpClient
import net.thechance.mena.core_chat.data.chat.utils.WebSocketManager
import net.thechance.mena.core_chat.data.contacts.createHttpClient


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class FakeWebSocketManager(
    client: HttpClient
) : WebSocketManager(
    client = client
) {

    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val incomingMessages: SharedFlow<String> = _incomingMessages

    var connected = false
        private set
    var lastSentFrame: String? = null
    var lastSubscription: String? = null
    var disconnected = false

    override fun connect(url: String, token: String, onConnected: suspend () -> Unit) {
        connected = true
        // You could immediately trigger onConnected if desired
    }

    override suspend fun disconnect() {
        disconnected = true
        connected = false
    }

    override suspend fun sendFrame(raw: String) {
        lastSentFrame = raw
    }

    override suspend fun sendConnectFrame() {
        lastSentFrame = "CONNECT_FRAME"
    }

    override suspend fun subscribe(destination: String, subscriptionId: String) {
        lastSubscription = destination
    }

    override suspend fun sendTextFrame(destination: String, payload: String) {
        lastSentFrame = "SEND to $destination with $payload"
    }

    override fun isConnected(): Boolean = connected

    suspend fun emitIncoming(message: String) {
        _incomingMessages.emit(message)
    }

    fun reset() {
        connected = false
        disconnected = false
        lastSentFrame = null
        lastSubscription = null
    }
}
*/
