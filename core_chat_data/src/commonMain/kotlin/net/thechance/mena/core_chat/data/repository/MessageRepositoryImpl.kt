@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.core_chat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
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
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MessageRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val pendingMessageDao: PendingMessageDao,
    private val cachedMessageDao: CachedMessageDao,
    private val dataStore: DataStore<Preferences>,
    private val messageSenderFactory: MessageSenderFactory,
    private val json: Json,
) : MessageRepository {
    private val messageFlows = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<MarkMessageAsReadEvent>()
    private val markChatAsDeleted = MutableSharedFlow<DeleteChatEvent>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun loadMessages(chatId: Uuid, page: Int, pageSize: Int): PagedData<Message> {
        val cachedMessages = cachedMessageDao.getMessagesByChatIdWithOffset(
            chatId = chatId.toString(),
            offset = (page * pageSize),
            limit = pageSize
        )

        val messages = cachedMessages.map { it.toDomain() }
        if (page == 0 || cachedMessages.isEmpty()) {
            syncRemoteMessages(chatId, page, pageSize, messages)
        }

        val isLastPage = messages.size < pageSize && cachedMessages.isNotEmpty()

        val totalCachedItems = cachedMessageDao.getTotalMessagesCount(chatId.toString())

        return PagedData(
            data = messages,
            totalItems = totalCachedItems,
            isLastPage = isLastPage,
        )

    }

    private suspend fun syncRemoteMessages(chatId: Uuid, startingPage: Int, pageSize: Int, localMessages: List<Message>) {
        val localIds = localMessages.map { it.id }.toSet()
        var currentPage = startingPage
        var shouldContinueFetching = true

        val now = Clock.System.now().toString()
        val lastSyncTime = dataStore.data.map { it[LAST_SYNC_TIME_KEY] }.firstOrNull()

        if (startingPage == 0 && lastSyncTime == null) {
            dataStore.edit { preferences ->
                preferences[LAST_SYNC_TIME_KEY] = now
            }
        }

        do {
            try {
                val networkResponse = tryNetworkCall<PagedDataDto<MessageDto>>(
                    bodyType = typeInfo<PagedDataDto<MessageDto>>()
                ) {
                    client.get(getChatMessagesEndpoint(chatId)) {
                        parameter(PAGE_NUMBER_PARAMETER, currentPage)
                        parameter(PAGE_SIZE_PARAMETER, pageSize)
                    }
                } ?: return

                val remoteMessages = networkResponse.toPagedListOfMessages().data

                if (remoteMessages.isEmpty()) shouldContinueFetching = false

                val (existing, notExisting) = remoteMessages.partition { it.id in localIds }

                if (notExisting.isNotEmpty()) {
                    cachedMessageDao.insertAllMessages(notExisting.toCachedMessageLocalDto())

                    messageFlows.emitAll(notExisting.asFlow())
                }

                if (existing.isNotEmpty()) shouldContinueFetching = false

                currentPage++

            } catch (e: Throwable) {
                if (currentPage == 0) return
                throw e
            }

        } while (shouldContinueFetching && localIds.isNotEmpty())

        if (localIds.isNotEmpty()) {
            syncAfterLastUpdate(chatId, now)
        }
    }

    suspend fun syncAfterLastUpdate(chatId: Uuid, newSyncTime: String) {
        val lastSyncTime = dataStore.data.map { it[LAST_SYNC_TIME_KEY] }.firstOrNull() ?: return

        val response = tryNetworkCall<List<MessageDto>>(
            bodyType = typeInfo<PagedDataDto<MessageDto>>()
        ) {
            client.get(getMessagesUpdatesEndPoint(chatId)) {
                parameter(UPDATED_AFTER_PARAMETER, Instant.parse(lastSyncTime))
            }
        }

        if (response != null) {
            dataStore.edit { preferences ->
                preferences[LAST_SYNC_TIME_KEY] = newSyncTime
            }

            messageFlows.emitAll(response.mapNotNull (MessageDto::toDomain ).asFlow())
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
    override fun observeConnectionStatus(): Flow<Boolean> {
        return webSocketManager.connectionStatus
    }

    private companion object {
        val LAST_SYNC_TIME_KEY = stringPreferencesKey("last_sync_time")
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val UPDATED_AFTER_PARAMETER = "updatedAfter"
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val PRIVATE_MESSAGES = "/private/messages"

        fun getChatMessagesEndpoint(chatId:Uuid): String {
            return "/chat/${chatId}/messages"
        }

        fun getMessagesUpdatesEndPoint(chatId: Uuid): String = "/chat/${chatId}/messages/updates"
    }
}