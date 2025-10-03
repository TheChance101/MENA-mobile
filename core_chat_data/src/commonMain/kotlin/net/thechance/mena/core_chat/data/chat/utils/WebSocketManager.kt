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


class WebSocketManager(
    private val client: HttpClient,
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var session: DefaultClientWebSocketSession? = null
    private var isActiveSession = false
    private var shouldReconnect = true

    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<String> = _incomingMessages

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("WebSocket Error: ${throwable.message}")
        isActiveSession = false
    }

    fun connect(
        url: String,
        token: String,
        onConnected: suspend () -> Unit = {}
    ) {
        if (isActiveSession) return

        scope.launch(exceptionHandler) {
            while (shouldReconnect) {
                try {
                    client.webSocket(urlString = url, request = { bearerAuth(token) }) {
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
                    delay(5000) // wait 5s before retry
                }
            }
        }
    }

    suspend fun disconnect() {
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

    suspend fun sendFrame(raw: String) {
        session?.send(Frame.Text(raw))
    }

    suspend fun sendConnectFrame() {
        sendFrame("CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\u0000")
    }

    suspend fun subscribe(destination: String, subscriptionId: String = "sub-0") {
        val frame = "SUBSCRIBE\nid:$subscriptionId\ndestination:$destination\n\n\u0000"
        sendFrame(frame)
    }

    suspend fun sendTextFrame(destination: String, payload: String) {
        val frameText = "SEND\ndestination:$destination\n\n$payload\n\n\u0000"
        sendFrame(frameText)
    }

    fun isConnected(): Boolean = isActiveSession
}