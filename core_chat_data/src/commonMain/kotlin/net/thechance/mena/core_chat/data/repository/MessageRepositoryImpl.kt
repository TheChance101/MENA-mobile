package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.messagesender.MessageSenderFactory
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadRequest
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionRequestDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.events.DeleteChatDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfMessages
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MessageRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val messageDao: MessageDao,
    private val messageSenderFactory: MessageSenderFactory,
    private val json: Json,
) : MessageRepository {
    private val messagesFlow = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<MarkMessageAsReadEvent>()
    private val markChatAsDeleted = MutableSharedFlow<DeleteChatEvent>()
    private val addReactionFlow = MutableSharedFlow<MessageReaction>()
    private val deleteReactionFlow = MutableSharedFlow<MessageReaction>()
    private val scope = CoroutineScope(Dispatchers.IO)


    override suspend fun loadMessages(chatId: Uuid, page: Int, pageSize: Int): PagedData<Message> {
        return tryNetworkCall<PagedDataDto<MessageDto>>(
            bodyType = typeInfo<PagedDataDto<MessageDto>>()
        ) {
            client.get(getChatMessagesEndpoint(chatId)){
                parameter(PAGE_NUMBER_PARAMETER, page)
                parameter(PAGE_SIZE_PARAMETER, pageSize)
            }
        }?.toPagedListOfMessages() ?: throw NotFoundException("Response body is null")
    }

    override suspend fun deleteMessage(message: Message) {
        messageDao.deleteMessage(message.id.toString())
    }

    override fun observePendingMessagesByChatId(chatId: Uuid): Flow<List<Message>> {
        val failedEntities = messageDao.getMessagesByChat(chatId.toString())
        return failedEntities.map { it.toDomain() }
    }

    override fun observeMessagesForChatOrAll(chatId: Uuid?): Flow<Message> {
        if (webSocketManager.isConnected().not()) initializeWebsocketConnection()
        return messagesFlow.filter { chatId == null || it.chatId == chatId }
    }

    override suspend fun sendMessage(message: Message) {
        val pendingMessage = message.copy(status = MessageStatus.LOADING).toLocalDto()
        messageDao.insertMessage(pendingMessage)

        try {
            val messageSender = messageSenderFactory.create(message.content)
            messageSender.send(message)
            messageDao.deleteMessage(pendingMessage.id)
        } catch (e: Exception) {
            messageDao.updateMessageStatus(pendingMessage.id, MessageLocalDto.MessageStatus.FAILED)
            throw SendMessageFailedException("Failed to send message: ${e.message}")
        }
    }

    override fun observeReadMessages(): Flow<MarkMessageAsReadEvent> {
        return markMessagesAsRead
    }

    override fun observeDeleteChat(): Flow<DeleteChatEvent> {
        return markChatAsDeleted
    }

    private fun initializeWebsocketConnection() {
        scope.launch {
            webSocketManager.connect(
                onConnected = ::onConnectedWebSocket
            )

            webSocketManager.incomingMessages.collect { handleIncomingAsEvent(it) }
        }
    }


    private suspend fun onConnectedWebSocket() {
        webSocketManager.subscribe(WEB_SOCKETS_USER_DESTINATION_PREFIX + PRIVATE_MESSAGES)
        webSocketManager.subscribe(WEB_SOCKETS_USER_DESTINATION_PREFIX + MARK_AS_READ)
        webSocketManager.subscribe(WEB_SOCKETS_USER_DESTINATION_PREFIX + ADD_REACTION)
        webSocketManager.subscribe(WEB_SOCKETS_USER_DESTINATION_PREFIX + REMOVE_REACTION)
        webSocketManager.subscribe(WEB_SOCKETS_USER_DESTINATION_PREFIX + DELETE_CHAT)
    }

    private suspend fun handleIncomingAsEvent(incomingText: String) {
        val parts = incomingText.split("\n\n", limit = 2)
        val headers = parts.getOrNull(0).orEmpty()
        val body = parts.getOrNull(1).orEmpty().trimEnd('\u0000')

        val destination = headers
            .lineSequence()
            .firstOrNull { it.startsWith("destination:$WEB_SOCKETS_USER_DESTINATION_PREFIX") }
            ?.substringAfter("destination:$WEB_SOCKETS_USER_DESTINATION_PREFIX")
            ?.trim()

        handleDestinations(body, destination.orEmpty())
    }

    private suspend fun handleDestinations(body: String, destination: String) {
        when (destination) {
            ADD_REACTION -> {
                val dto = json.decodeFromString<MessageReactionDto>(body)
                addReactionFlow.emit(dto.toDomain())
            }

            REMOVE_REACTION -> {
                val dto = json.decodeFromString<MessageReactionDto>(body)
                deleteReactionFlow.emit(dto.toDomain())
            }

            PRIVATE_MESSAGES -> {
                val dto = json.decodeFromString<MessageDto>(body)
                dto.toDomain()?.let { messagesFlow.emit(it) }
            }

            MARK_AS_READ -> {
                val dto = json.decodeFromString<MarkAsReadDto>(body)
                markMessagesAsRead.emit(dto.toDomain())
            }

            DELETE_CHAT -> {
                val dto = json.decodeFromString<DeleteChatDto>(body)
                markChatAsDeleted.emit(dto.toDomain())
            }

            else -> {
                println("Unknown destination: $destination")
            }
        }

    }

    override suspend fun markMessagesOfChatAsRead(chatId: Uuid) {
        webSocketManager.sendTextFrame(
            destination = MARK_AS_READ_DESTINATION,
            payload = json.encodeToString<MarkAsReadRequest>(MarkAsReadRequest(chatId = chatId.toString()))
        )
    }

    override suspend fun addMessageReaction(messageId: Uuid, emoji: String) {
        sendMessageReactionEvent(ADD_REACTION_DESTINATION, messageId, emoji)
    }

    override suspend fun removeMessageReaction(messageId: Uuid, emoji: String) {
        sendMessageReactionEvent(REMOVE_REACTION_DESTINATION, messageId, emoji)
    }

    override fun observeMessageReactions(): Flow<MessageReaction> { return addReactionFlow }

    override fun observeRemovedMessageReactions(): Flow<MessageReaction> { return deleteReactionFlow }

    private suspend fun sendMessageReactionEvent(
        destination: String,
        messageId: Uuid,
        emoji: String
    ) {
        val dto = MessageReactionRequestDto(messageId = messageId.toString(), emoji = emoji)
        val payload = json.encodeToString<MessageReactionRequestDto>(dto)

        if (!webSocketManager.isConnected()) {
            throw SendMessageFailedException("WebSocket is not connected")
        }

        webSocketManager.sendTextFrame(destination, payload)
    }

    private companion object {
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val PRIVATE_MESSAGES = "/private/messages"

        const val MARK_AS_READ = "/private/markAsRead"
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"

        const val ADD_REACTION = "/private/addReaction"
        const val ADD_REACTION_DESTINATION = "/app/chat.addMessageReaction"
        const val REMOVE_REACTION = "/private/deleteReaction"
        const val REMOVE_REACTION_DESTINATION = "/app/chat.deleteMessageReaction"

        const val DELETE_CHAT = "/private/deleteChat"

        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"

        fun getChatMessagesEndpoint(chatId:Uuid): String {
            return "/chat/${chatId}/messages"
        }
    }
}