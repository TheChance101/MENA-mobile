package net.thechance.mena.core_chat.data.chat.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.bearerAuth
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.data.network.ApiConstants.WEB_SOCKETS_ENDPOINT


class WebSocketManagerImpl(
    private val baseUrl: String,
    private val client: HttpClient,
) : WebSocketManager {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var session: DefaultClientWebSocketSession? = null
    private var isActiveSession = false
    private var shouldReconnect = true

    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val incomingMessages: SharedFlow<String> = _incomingMessages

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("WebSocket Error: ${throwable.message}")
        isActiveSession = false
    }

    override fun connect(
        token: String,
        onConnected: suspend () -> Unit
    ) {
        if (isActiveSession) return

        scope.launch(exceptionHandler) {
            while (shouldReconnect) {
                try {
                    client.webSocket(
                        urlString = getConstructWebSocketUrl(baseUrl),
                        request = { bearerAuth(token) }
                    ) {
                        session = this
                        isActiveSession = true
                        sendConnectFrame()
                        println("WebSocket connected")

                        // Listen for incoming frames
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                when {
                                    text.startsWith("CONNECTED") -> onConnected()
                                    text.startsWith("MESSAGE") -> _incomingMessages.emit(text)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("WebSocket reconnect error: ${e.message}")
                    isActiveSession = false
                    session = null
                    delay(RECONNECT_DELAY)
                }
            }
        }
    }

    override suspend fun disconnect() {
        try {
            if (isActiveSession) {
                sendFrame("DISCONNECT\n\n\u0000")
                println("STOMP DISCONNECT sent")
            }
            session?.close()
            println("WebSocket closed by client")
        } catch (e: Exception) {
            println("Error while disconnecting: ${e.message}")
        } finally {
            isActiveSession = false
            session = null
        }
    }

    private suspend fun sendFrame(raw: String) {
        session?.send(Frame.Text(raw))
    }

    private suspend fun sendConnectFrame() {
        sendFrame("CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\u0000")
    }

    override suspend fun subscribe(destination: String) {
        val frame = "SUBSCRIBE\nid:sub-0\ndestination:$destination\n\n\u0000"
        sendFrame(frame)
    }

    override suspend fun sendTextFrame(destination: String, payload: String) {
        val frameText = "SEND\ndestination:$destination\n\n$payload\n\n\u0000"
        sendFrame(frameText)
    }

    override fun isConnected(): Boolean = isActiveSession

    private fun getConstructWebSocketUrl(baseUrl: String): String {
        return "${
            baseUrl
                .replace("https", "wss")
                .replace("http", "ws")
        }$WEB_SOCKETS_ENDPOINT"
    }

    private companion object {
        const val RECONNECT_DELAY = 5000L
    }
}