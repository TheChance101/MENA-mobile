package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadRequest
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.SendMessageDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfChatSummary
import net.thechance.mena.core_chat.data.source.remote.network.ImageDownloader
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.utils.MessageEvent
import net.thechance.mena.core_chat.data.utils.buildMultiPartFormData
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.OperationFailedException
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val messageDao: MessageDao,
    private val json: Json,
    private val imageDownloader: ImageDownloader,
) : ChatRepository, BaseRepository {

    private val messageFlows = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<String>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun loadMessages(chatId: Uuid): List<Message> {
        return tryNetworkCall<PagedDataDto<MessageDto>>(
            bodyType = typeInfo<PagedDataDto<MessageDto>>()
        ) {
            client.get(CHAT_HISTORY_ENDPOINT) {
                parameter(CHAT_ID_PARAMETER, chatId)
                parameter(PAGE_NUMBER_PARAMETER, PAGE_NUMBER)
                parameter(PAGE_SIZE_PARAMETER, PAGE_SIZE)
            }
        }?.data?.mapNotNull { it.toDomain() } ?: emptyList()
    }

    override suspend fun getChatsSummary(pageNumber: Int, pageSize: Int): PagedData<ChatSummary> {
        return tryNetworkCall<PagedDataDto<ChatSummaryDto>>(
            bodyType = typeInfo<PagedDataDto<ChatSummaryDto>>()
        ) {
            client.get(CHAT_SUMMARY_ENDPOINT) {
                parameter(PAGE_NUMBER_PARAMETER, pageNumber)
                parameter(PAGE_SIZE_PARAMETER, pageSize)
            }
        }?.toPagedListOfChatSummary() ?: throw NotFoundException("Response body is null")
    }

    override suspend fun deleteMessage(message: Message) {
        messageDao.deleteMessage(message.id.toString())
    }

    override suspend fun getChatByContactUserId(userId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(
            bodyType = typeInfo<ChatDto>()
        ) {
            client.get(CHAT_ENDPOINT) {
                parameter(RECEIVER_ID_PARAMETER, userId)
            }
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }

    override suspend fun getChatById(chatId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(bodyType = typeInfo<ChatDto>()) {
            client.get("$CHAT_ENDPOINT/$chatId")
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }

    override suspend fun getLocalMessages(chatId: Uuid): List<Message> {
        val failedEntities = messageDao.getMessagesByChat(chatId.toString())
        return failedEntities.map { it.toDomain() }
    }

    override suspend fun downloadImage(url: String) {
        val success = imageDownloader.downloadImageToGallery(url)
        if (!success) {
            throw OperationFailedException("Failed to download image")
        }
    }

    override fun subscribeToMessages(chatId: Uuid): Flow<Message> {
        scope.launch {
            initializeWebsocketConnection(chatId.toString())
        }
        return messageFlows
    }

    override suspend fun sendMessage(message: Message) {
        val updatedMessage = message.copy(status = MessageStatus.LOADING).toLocalDto()
        messageDao.insertMessage(updatedMessage)

        try {
            when (val content = message.content) {
                is MessageContent.Text -> {
                    val sendMessageDto = SendMessageDto(
                        chatId = message.chatId.toString(),
                        text = content.text
                    )
                    sendTextMessage(sendMessageDto)
                }

                is MessageContent.Images -> {
                    val source = content.source
                    val byteArrays =
                        if (source is ImagesSource.Local)
                            source.byteArrays
                        else throw SendMessageFailedException("Failed to send message: Corrupted images")
                    sendImagesMessage(
                        imageNames = byteArrays.mapIndexed { index, _ -> "image_$index" },
                        images = byteArrays,
                        chatId = message.chatId
                    )
                }
            }
        } catch (e: Exception) {
            messageDao.updateMessageStatus(
                updatedMessage.id,
                MessageLocalDto.MessageStatus.FAILED
            )
            throw SendMessageFailedException("Failed to send message: ${e.message}")
        }
        messageDao.deleteMessage(updatedMessage.id)
    }

    private suspend fun sendTextMessage(sendMessageDto: SendMessageDto) {
        if (webSocketManager.isConnected()) {
            val messageJson = json.encodeToString(
                SendMessageDto.serializer(),
                sendMessageDto
            )
            webSocketManager.sendTextFrame(
                destination = SEND_MESSAGE_DESTINATION,
                payload = messageJson

            )
        } else {
            throw SendMessageFailedException("Failed to send message")
        }
    }

    private suspend fun sendImagesMessage(
        imageNames: List<String>,
        images: List<ByteArray>,
        chatId: Uuid
    ): MessageDto {
        if (images.size != imageNames.size) throw SendMessageFailedException("imageNames and images must have the same size.")

        val files = imageNames.zip(images)

        return tryNetworkCall<MessageDto>(
            bodyType = typeInfo<MessageDto>()
        ) {
            client.post("$IMAGES_ENDPOINT/$chatId") {
                setBody(files.buildMultiPartFormData(fieldName = IMAGES_FILES_PARAM))
            }
        } ?: throw SendMessageFailedException("Failed to upload images")
    }

    override fun observeReadMessages(): Flow<String> {
        return markMessagesAsRead
    }

    private suspend fun initializeWebsocketConnection(chatId: String) {
        webSocketManager.connect(
            onConnected = { onConnectedWebSocket(chatId) }
        )

        webSocketManager.incomingMessages.collect { handleIncomingAsEvent(chatId, it) }
    }

    private suspend fun onConnectedWebSocket(chatId: String) {
        webSocketManager.subscribe("$WEB_SOCKETS_USER_DESTINATION_PREFIX/$chatId$QUEUE_MESSAGES")
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

    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val CHAT_ID_PARAMETER = "chatId"
        const val RECEIVER_ID_PARAMETER = "receiverId"
        const val PAGE_SIZE = 1000
        const val PAGE_NUMBER = 0
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val SEND_MESSAGE_DESTINATION = "/app/chat.privateMessage"
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val QUEUE_MESSAGES = "/queue/messages"
        const val CHAT_ENDPOINT = "/chat"
        const val IMAGES_ENDPOINT = "/chat/image"
        const val IMAGES_FILES_PARAM = "images"
        const val CHAT_HISTORY_ENDPOINT = "/chat/history"
        const val CHAT_SUMMARY_ENDPOINT = "/chat/chatsSummary"
    }
}