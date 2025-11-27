package net.thechance.mena.core_chat.data.source.remote.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


class WebSocketManagerImpl(
    private val baseUrl: String,
    private val clientHolder: HttpClientHolder,
) : WebSocketManager {

    private val client: HttpClient
        get() = clientHolder.getClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var session: DefaultClientWebSocketSession? = null
    private var isActiveSession = false
    private var shouldReconnect = false
    private var connectionJob: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, _ -> isActiveSession = false }
    private val _connectionStatus = MutableStateFlow(false)
    override val connectionStatus = _connectionStatus.asStateFlow()
    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val incomingMessages: SharedFlow<String> = _incomingMessages

    private var subscriptionCounter = 0

    override fun connect(
        onConnected: suspend () -> Unit
    ) {
        if (isActiveSession) return
        shouldReconnect = true

        connectionJob = scope.launch(exceptionHandler) {
            while (shouldReconnect && isActive) {
                try {
                    performHandShake(onConnected)
                } catch (_: CancellationException) {
                    break
                } catch (_: Exception) {
                    isActiveSession = false
                    _connectionStatus.emit(false)
                    session = null
                    if (shouldReconnect) delay(RECONNECT_DELAY)
                }
            }
        }
    }

    private suspend fun performHandShake(
        onConnected: suspend () -> Unit
    ) {
        client.webSocket(
            urlString = getConstructWebSocketUrl(baseUrl),
        ) {
            session = this
            isActiveSession = true
            _connectionStatus.emit(true)
            sendConnectFrame()
            listenForIncomingFrames(incoming, onConnected)
        }
    }

    private suspend fun DefaultClientWebSocketSession.listenForIncomingFrames(
        incoming: ReceiveChannel<Frame>,
        onConnected: suspend () -> Unit
    ) {
        for (frame in incoming) {
            if (!isActive) break
            if (frame is Frame.Text) {
                val text = frame.readText()
                when {
                    text.startsWith("CONNECTED") -> onConnected()
                    text.startsWith("MESSAGE") -> _incomingMessages.emit(text)
                }
            }
        }
    }

    override suspend fun disconnect() {
        shouldReconnect = false
        session?.close()

        isActiveSession = false
        session = null

        _connectionStatus.emit(false)
        connectionJob?.cancelAndJoin()
        connectionJob = null
    }

    private suspend fun sendFrame(raw: String) {
        session?.send(Frame.Text(raw))
    }

    private suspend fun sendConnectFrame() {
        sendFrame(
            CONNECTION_FRAME
        )
    }

    override suspend fun subscribe(destination: String) {
        val id = "sub-${subscriptionCounter++}"
        val frame =
            "$SUBSCRIBE\n" +
                    "id:$id\n" +
                    "$DESTINATION:$destination\n" +
                    "\n\u0000"
        sendFrame(frame)
    }

    override suspend fun sendTextFrame(destination: String, payload: String) {
        val frameText =
            "$SEND\n" +
                    "$DESTINATION:$destination\n" +
                    "\n" +
                    "$payload\n" +
                    "\n\u0000"
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
        const val WEB_SOCKETS_ENDPOINT = "/ws"
        const val RECONNECT_DELAY = 5000L
        const val SUBSCRIBE = "SUBSCRIBE"
        const val SEND = "SEND"
        const val DESTINATION = "destination"
        const val CONNECTION_FRAME = "CONNECT\n" +
                "accept-version:1.2\n" +
                "heart-beat:10000,10000\n" +
                "\n\u0000"
    }
}