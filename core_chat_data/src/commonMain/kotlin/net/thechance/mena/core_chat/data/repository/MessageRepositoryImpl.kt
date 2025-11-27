@file:OptIn(ExperimentalTime::class)

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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.messagesender.MessageSenderFactory
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageDao
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.local.database.chatSyncTime.ChatSyncTime
import net.thechance.mena.core_chat.data.source.local.database.chatSyncTime.ChatSyncTimeDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageDao
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadRequest
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionRequestDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.events.DeleteChatDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toCachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toListOfMessages
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfMessages
import net.thechance.mena.core_chat.data.source.remote.mapper.toPendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.service.QuranService
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MessageRepositoryImpl(
    private val clientHolder: HttpClientHolder,
    private val webSocketManager: WebSocketManager,
    private val pendingMessageDao: PendingMessageDao,
    private val cachedMessageDao: CachedMessageDao,
    private val chatSyncTimeDao: ChatSyncTimeDao,
    private val quranService: QuranService,
    private val authRepository: AuthenticationRepository,
    private val messageSenderFactory: MessageSenderFactory,
    private val json: Json,
) : MessageRepository {
    private val client: HttpClient
        get() = clientHolder.getClient()
    private val messagesFlow = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<MarkMessageAsReadEvent>()
    private val markChatAsDeleted = MutableSharedFlow<DeleteChatEvent>()
    private val addReactionFlow = MutableSharedFlow<MessageReaction>()
    private val deleteReactionFlow = MutableSharedFlow<MessageReaction>()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        observeAuthenticationState()
    }
    override suspend fun loadMessages(chatId: Uuid, page: Int, pageSize: Int): PagedData<Message> {

        val cachedMessages = cachedMessageDao.getMessagesByChatIdWithOffset(
            chatId = chatId.toString(),
            offset = (page * pageSize),
            limit = pageSize
        )

        val messages = cachedMessages.map(CachedMessageLocalDto::toDomain).getSurahsNames()

        if (messages.isEmpty()) {
            return getFromRemote(chatId, page, pageSize)
        }

        val now = Clock.System.now().toString()
        val lastSyncTime = chatSyncTimeDao.getLastSyncTime(chatId.toString())
        if (lastSyncTime != null) {
            syncAfterLastUpdate(chatId)
        } else {
            chatSyncTimeDao.upsert(ChatSyncTime(chatId.toString(), now))
        }

        val totalCachedItems = cachedMessageDao.getTotalMessagesCount(chatId.toString())
        return PagedData(
            data = messages,
            totalItems = totalCachedItems,
            isLastPage = false,
        )
    }

    private suspend fun getFromRemote(
        chatId: Uuid,
        page: Int,
        pageSize: Int
    ): PagedData<Message> {
        val response = tryNetworkCall<PagedDataDto<MessageDto>>(
            bodyType = typeInfo<PagedDataDto<MessageDto>>()
        ) {
            client.get(getChatMessagesEndpoint(chatId)) {
                parameter(PAGE_NUMBER_PARAMETER, page)
                parameter(PAGE_SIZE_PARAMETER, pageSize)
            }
        }

        val page = response.toPagedListOfMessages()
        updateLocalMessages(page.data)

        return page.copy(data = page.data.getSurahsNames())
    }

    private suspend fun updateLocalMessages(messages: List<Message>){
        cachedMessageDao.insertAllMessages(messages.toCachedMessageLocalDto())

        val messagesIds = messages.map{ it.id.toString() }
        pendingMessageDao.deleteMessagesByIds(messagesIds)
    }

    suspend fun syncAfterLastUpdate(chatId: Uuid) {
        try {
            val now = Clock.System.now()
            val lastSyncTime = chatSyncTimeDao.getLastSyncTime(chatId.toString()) ?: return

            var page = 0
            var isLastPage = false

            while (!isLastPage) {

                val response = tryNetworkCall<PagedDataDto<MessageDto>>(
                    bodyType = typeInfo<PagedDataDto<MessageDto>>()
                ) {
                    client.get(getMessagesUpdatesEndPoint(chatId)) {
                        parameter(LAST_UPDATE_TIME_PARAMETER, Instant.parse(lastSyncTime))
                        parameter(PAGE_NUMBER_PARAMETER, page)
                        parameter(PAGE_SIZE_PARAMETER, DEFAULT_PAGE_SIZE)
                    }
                }

                if (response.data.isNotEmpty()) {
                    chatSyncTimeDao.upsert(ChatSyncTime(chatId.toString(), now.toString()))

                    updateLocalMessages(response.data.toListOfMessages())

                    messagesFlow.emitAll(response.data.toListOfMessages().getSurahsNames().asFlow())
                }

                isLastPage = response.toPagedListOfMessages().isLastPage
                page++
            }
        } catch (e: Throwable) {
            println("Sync Messages After Last Update Error : ${e.printStackTrace()}")
        }
    }

    override suspend fun deleteMessageById(messageId: Uuid) {
        pendingMessageDao.deleteMessageById(messageId.toString())
    }

    override fun observePendingMessagesByChatId(chatId: Uuid): Flow<List<Message>> {
        val messages = pendingMessageDao.getMessagesByChatId(chatId.toString())
        return messages.map { it.toDomain() }
    }

    override fun observeMessagesForChatOrAll(chatId: Uuid?): Flow<Message> {
        if (webSocketManager.isConnected().not()) initializeWebsocketConnection()
        return messagesFlow.filter { chatId == null || it.chatId == chatId }
    }

    override suspend fun sendMessage(message: Message) {
        val pendingMessage = message.copy(status = MessageStatus.LOADING).toPendingMessageLocalDto()
        pendingMessageDao.insertMessage(pendingMessage)

        try {
            val messageSender = messageSenderFactory.create(message.content)
            messageSender.send(message)
            pendingMessageDao.deleteMessageById(pendingMessage.id)
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
                val reaction = json.decodeFromString<MessageReactionDto>(body).toDomain()

                val message =
                    cachedMessageDao.getMessageById(reaction.messageId.toString()) ?: return
                val updatedReactions = message.reactions.toMutableList().apply {
                    removeAll { it.userId == reaction.userId && it.emoji == reaction.emoji }
                    add(reaction.toLocalDto())
                }
                cachedMessageDao.updateMessage(message.copy(reactions = updatedReactions))

                addReactionFlow.emit(reaction)
            }

            REMOVE_REACTION -> {
                val reaction = json.decodeFromString<MessageReactionDto>(body).toDomain()

                val message =
                    cachedMessageDao.getMessageById(reaction.messageId.toString()) ?: return
                val updatedReactions = message.reactions.filterNot { it.userId == reaction.userId }
                cachedMessageDao.updateMessage(message.copy(reactions = updatedReactions))

                deleteReactionFlow.emit(reaction)
            }

            PRIVATE_MESSAGES -> {
                val message = json.decodeFromString<MessageDto>(body).toDomain()
                message.let {
                    updateLocalMessages(listOf(message))
                    messagesFlow.emit(it.getMessageWithSurahName())
                }
            }

            MARK_AS_READ -> {
                val dto = json.decodeFromString<MarkAsReadDto>(body)
                cachedMessageDao.markMessagesAsReadByReader(dto.chatId, dto.readByUserId)
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

    override fun observeConnectionStatus(chatId: Uuid): Flow<Boolean> {
        return webSocketManager.connectionStatus.onEach { isConnected ->
            if (isConnected) syncAfterLastUpdate(chatId)
        }
    }

    override suspend fun addMessageReaction(messageId: Uuid, emoji: String) {
        sendMessageReactionEvent(ADD_REACTION_DESTINATION, messageId, emoji)
    }

    override suspend fun removeMessageReaction(messageId: Uuid, emoji: String) {
        sendMessageReactionEvent(REMOVE_REACTION_DESTINATION, messageId, emoji)
    }

    override fun observeMessageReactions(): Flow<MessageReaction> {
        return addReactionFlow
    }

    override fun observeRemovedMessageReactions(): Flow<MessageReaction> {
        return deleteReactionFlow
    }

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

    private suspend fun List<Message>.getSurahsNames(): List<Message> {
        return map { it.getMessageWithSurahName() }
    }

    private suspend fun Message.getMessageWithSurahName(): Message {
        return when(val content = this.content) {
            is MessageContent.Ayah -> {
                this.copy(content = content.copy(surahName = getSurahNameById(surahId = content.surahId)))
            }
            else -> this
        }
    }

    private suspend fun getSurahNameById(surahId: Int): String {
        val surah: Surah = quranService.getSurahDetails(surahId)
        return surah.name
    }

    private fun observeAuthenticationState() {
        scope.launch {
            authRepository.observeTokenChange().collectLatest { token ->
                if (token.isEmpty()) {
                    clearAllMessagesCache()
                }
            }
        }
    }

    private suspend fun clearAllMessagesCache() {
        try {
            webSocketManager.disconnect()
            cachedMessageDao.clearAllMessages()
            pendingMessageDao.clearAllPendingMessages()
            chatSyncTimeDao.clearAllSyncTimes()

        } catch (e:Throwable) {
            println("MessageRepository ERROR: Failed to clear cache. Error: ${e.message}")
        }
    }
    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val LAST_UPDATE_TIME_PARAMETER = "lastUpdateTime"
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val PRIVATE_MESSAGES = "/private/messages"
        const val MARK_AS_READ = "/private/markAsRead"
        const val ADD_REACTION = "/private/addReaction"
        const val ADD_REACTION_DESTINATION = "/app/chat.addMessageReaction"
        const val REMOVE_REACTION = "/private/deleteReaction"
        const val REMOVE_REACTION_DESTINATION = "/app/chat.deleteMessageReaction"
        const val DELETE_CHAT = "/private/deleteChat"

        const val DEFAULT_PAGE_SIZE = 100

        fun getChatMessagesEndpoint(chatId: Uuid): String {
            return "/chat/${chatId}/messages"
        }

        fun getMessagesUpdatesEndPoint(chatId: Uuid): String = "/chat/${chatId}/messages/latest"
    }
}