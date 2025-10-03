package net.thechance.mena.core_chat.data.chat

import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.chat.dto.ChatDto
import net.thechance.mena.core_chat.data.chat.dto.MarkAsReadRequest
import net.thechance.mena.core_chat.data.chat.dto.MessageDto
import net.thechance.mena.core_chat.data.chat.dto.SendMessageDto
import net.thechance.mena.core_chat.data.chat.utils.WebSocketManager
import net.thechance.mena.core_chat.data.chat.dto.MessageEvent
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_HISTORY_ENDPOINT
import net.thechance.mena.core_chat.data.shared.BaseRepository
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val authenticationRepository: AuthenticationRepository,
    private val json: Json,
) : ChatRepository, BaseRepository {

    private val messageFlows = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<String>()
    private val scope = CoroutineScope(Dispatchers.IO)


    override suspend fun loadMessages(chatId: Uuid): List<Message> {
        return tryNetworkCall<PagedDataDto<MessageDto>>(
            bodyType = typeInfo<PagedDataDto<MessageDto>>()
        ) {
            val token = authenticationRepository.getAccessToken()

            client.get(CHAT_HISTORY_ENDPOINT) {
                parameter(CHAT_ID_PARAMETER, chatId)
                parameter(PAGE_NUMBER_PARAMETER, PAGE_NUMBER)
                parameter(PAGE_SIZE_PARAMETER, PAGE_SIZE)
                bearerAuth(token)
            }
        }?.data?.mapNotNull { it.toDomain() } ?: emptyList()
    }

    override suspend fun getChatByContactUserId(userId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(
            bodyType = typeInfo<ChatDto>()
        ) {
            val token = authenticationRepository.getAccessToken()

            client.get(CHAT_ENDPOINT) {
                parameter(RECEIVER_ID_PARAMETER, userId)
                bearerAuth(token)
            }
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }

    override fun subscribeToMessages(chatId: Uuid): Flow<Message> {
        scope.launch {
            initializeWebsocketConnection(chatId.toString())
        }
        return messageFlows
    }

    override suspend fun sendMessage(message: Message) {
        if (webSocketManager.isConnected()) {
            webSocketManager.sendTextFrame(
                destination = SEND_MESSAGE_DESTINATION,
                payload = json.encodeToString<SendMessageDto>(message.toSendMessageRequestDto())
            )
        } else {
            throw SendMessageFailedException("Failed to send message")
        }
    }

    override fun observeReadMessages(): Flow<String> {
        return markMessagesAsRead
    }

    private suspend fun initializeWebsocketConnection(chatId: String) {
        val bearerToken = authenticationRepository.getAccessToken()

        webSocketManager.connect(
            token = bearerToken,
            onConnected = { onConnectedWebSocket(chatId) }
        )

        webSocketManager.incomingMessages.collect { handleIncomingAsEvent(chatId, it) }
    }

    private suspend fun onConnectedWebSocket(chatId: String) {
        webSocketManager.subscribe("$WEB_SOCKETS_APPLICATION_DESTINATION_PREFIX/$chatId$QUEUE_MESSAGES")
        markMessageAsRead(chatId)
    }

    private suspend fun handleIncomingAsEvent(
        chatId: String,
        incomingText: String
    ) {
        val jsonBody = incomingText.substringAfter("\n\n").trimEnd('\u0000')
        val event = json.decodeFromString<MessageEvent>(jsonBody)

        when (event) {
            is MessageEvent.MarkAsRead -> {
                markMessagesAsRead.emit(event.dto.readBy)
            }

            is MessageEvent.Message -> {
                event.dto.toDomain()?.let { messageFlows.emit(it) }
                markMessageAsRead(chatId)
            }
        }
    }

    private suspend fun markMessageAsRead(chatId: String) {
        webSocketManager.sendTextFrame(
            destination = MARK_AS_READ_DESTINATION,
            payload = json.encodeToString<MarkAsReadRequest>(MarkAsReadRequest(chatId = chatId))
        )
    }

    override suspend fun disconnect() {
        webSocketManager.disconnect()
    }

    private companion object{
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val CHAT_ID_PARAMETER = "chatId"
        const val RECEIVER_ID_PARAMETER = "receiverId"
        const val PAGE_SIZE = 1000
        const val PAGE_NUMBER = 0
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val SEND_MESSAGE_DESTINATION = "/app/chat.privateMessage"
        const val WEB_SOCKETS_APPLICATION_DESTINATION_PREFIX = "/user"
        const val QUEUE_MESSAGES = "/queue/messages"
    }
}