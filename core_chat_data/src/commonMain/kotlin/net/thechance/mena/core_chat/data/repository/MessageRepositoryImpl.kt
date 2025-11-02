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
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageDao
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadRequest
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toCachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toEntity
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfMessages
import net.thechance.mena.core_chat.data.source.remote.mapper.toPendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.data.utils.MessageEvent
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MessageRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val pendingMessageDao: PendingMessageDao,
    private val cachedMessageDao: CachedMessageDao,
    private val messageSenderFactory: MessageSenderFactory,
    private val json: Json,
) : MessageRepository {
    private val messageFlows = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<MarkMessageAsReadEvent>()
    private val markChatAsDeleted = MutableSharedFlow<DeleteChatEvent>()
    private val scope = CoroutineScope(Dispatchers.IO)


    override suspend fun loadMessages(chatId: Uuid, page: Int, pageSize: Int): PagedData<Message> {
        val networkResponse = tryNetworkCall<PagedDataDto<MessageDto>>(
            bodyType = typeInfo<PagedDataDto<MessageDto>>()
        ) {
            client.get(CHAT_HISTORY_ENDPOINT) {
                parameter(CHAT_ID_PARAMETER, chatId)
                parameter(PAGE_NUMBER_PARAMETER, page)
                parameter(PAGE_SIZE_PARAMETER, pageSize)
            }
        }

        if (networkResponse != null) {
            val pagedMessages = networkResponse.toPagedListOfMessages()
            val messages = pagedMessages.data.toCachedMessageLocalDto()
            cachedMessageDao.insertAllMessages(messages)
            return pagedMessages
        } else {

            val cachedMessages = cachedMessageDao.getMessagesByChatIdWithOffset(
                chatId = chatId.toString(),
                offset = (page * pageSize),
                limit = pageSize
            )

            if (cachedMessages.isEmpty() && page > 0) {
                return PagedData(data = emptyList(), isLastPage = true, totalItems = 0)
            }

            val messages = cachedMessages.map { it.toDomain() }
            val isLastPage = messages.size < pageSize

            val estimatedTotal = if (isLastPage) {
                (page * pageSize) + messages.size
            } else {
                (page + 1) * pageSize + 1
            }
            return PagedData(
                data = messages,
                totalItems = estimatedTotal,
                isLastPage = isLastPage,
            )
        }
    }

    override suspend fun deleteMessage(message: Message) {
        pendingMessageDao.deleteMessage(message.id.toString())
    }

    override fun observePendingMessagesByChatId(chatId: Uuid): Flow<List<Message>> {
        val messages = pendingMessageDao.getMessagesByChat(chatId.toString())
        return messages.map { it.toDomain() }
    }

    override fun observeMessagesForChatOrAll(chatId: Uuid?): Flow<Message> {
        if (webSocketManager.isConnected().not()) initializeWebsocketConnection()
        return messageFlows.filter { chatId == null || it.chatId == chatId }
    }

    override suspend fun sendMessage(message: Message) {
        val pendingMessage = message.copy(status = MessageStatus.LOADING).toPendingMessageLocalDto()
        pendingMessageDao.insertMessage(pendingMessage)

        try {
            val messageSender = messageSenderFactory.create(message.content)
            messageSender.send(message)
            pendingMessageDao.deleteMessage(pendingMessage.id)
        } catch (e: Exception) {
            pendingMessageDao.updateMessageStatus(pendingMessage.id, MessageStatus.FAILED)
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
            webSocketManager.connect(onConnected = ::onConnectedWebSocket)

            webSocketManager.incomingMessages.collect { handleIncomingAsEvent(it) }
        }
    }


    private suspend fun onConnectedWebSocket() {
        webSocketManager.subscribe(WEB_SOCKETS_USER_DESTINATION_PREFIX + PRIVATE_MESSAGES)
    }

    private suspend fun handleIncomingAsEvent(
        incomingText: String
    ) {
        val jsonBody = incomingText.substringAfter("\n\n").trimEnd('\u0000')
        val event = json.decodeFromString<MessageEvent>(jsonBody)

        when (event) {
            is MessageEvent.MarkAsRead -> {
                markMessagesAsRead.emit(event.dto.toEntity())
            }

            is MessageEvent.Message -> {
                event.dto.toDomain()?.let { messageFlows.emit(it) }
            }

            is MessageEvent.DeleteChat -> {
                markChatAsDeleted.emit(DeleteChatEvent(chatId = event.dto.deletedChatId))
            }
        }
    }

    override suspend fun markMessagesOfChatAsRead(chatId: Uuid) {
        webSocketManager.sendTextFrame(
            destination = MARK_AS_READ_DESTINATION,
            payload = json.encodeToString<MarkAsReadRequest>(MarkAsReadRequest(chatId = chatId.toString()))
        )
    }

    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val PRIVATE_MESSAGES = "/private/messages"
        const val CHAT_HISTORY_ENDPOINT = "/chat/history"
        const val CHAT_ID_PARAMETER = "chatId"
    }
}