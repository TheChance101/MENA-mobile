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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadRequest
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.SendMessageDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toEntity
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.utils.MessageEvent
import net.thechance.mena.core_chat.data.utils.buildImageMultiPartFormData
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MessageRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val messageDao: MessageDao,
    private val json: Json,
) : BaseRepository, MessageRepository {
    private val messageFlows = MutableSharedFlow<Message>()
    private val markMessagesAsRead = MutableSharedFlow<MarkMessageAsReadEvent>()
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

    override suspend fun deleteMessage(message: Message) {
        messageDao.deleteMessage(message.id.toString())
    }

    override fun getLocalMessages(chatId: Uuid): Flow<List<Message>> {
        val failedEntities = messageDao.getMessagesByChat(chatId.toString())
        return failedEntities.map { it.toDomain() }
    }

    override fun getMessages(chatId: Uuid?): Flow<Message> {
        if (webSocketManager.isConnected().not()) initializeWebsocketConnection()
        return messageFlows.filter { chatId == null || it.chatId == chatId }
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
                    var remainingImages = byteArrays.toMutableList()
                    sendImagesMessage(
                        imageNames = byteArrays.mapIndexed { index, _ -> "image_$index" },
                        images = byteArrays,
                        chatId = message.chatId,
                        onSuccessImage = { image ->
                            remainingImages = remainingImages.apply { remove(image) }
                            messageDao.updateMessageImages(
                                id = updatedMessage.id,
                                images = remainingImages
                            )
                        }
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
        chatId: Uuid,
        onSuccessImage: suspend (ByteArray) -> Unit = {}
    ) {
        if (images.size != imageNames.size)
            throw SendMessageFailedException("imageNames and images must have the same size.")

        val files = imageNames.zip(images)

        var messageId: String? = null

        files.forEachIndexed { index, imageFile ->
            val multipart = imageFile.buildImageMultiPartFormData(
                fieldName = IMAGES_FILES_PARAM,
                chatId = chatId.toString(),
                messageId = messageId
            )

            val messageResponse = tryNetworkCall<MessageDto>(
                bodyType = typeInfo<MessageDto>()
            ) {
                client.post(IMAGES_ENDPOINT) {
                    setBody(multipart)
                }
            }

            if (messageResponse != null) onSuccessImage(imageFile.second)

            if (messageId == null && messageResponse != null) {
                messageId = messageResponse.id
            }
        }

    }

    override fun observeReadMessages(): Flow<MarkMessageAsReadEvent> {
        return markMessagesAsRead
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
        }
    }

    override suspend fun markMessagesAsRead(chatId: Uuid) {
        webSocketManager.sendTextFrame(
            destination = MARK_AS_READ_DESTINATION,
            payload = json.encodeToString<MarkAsReadRequest>(MarkAsReadRequest(chatId = chatId.toString()))
        )
    }

    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val PAGE_SIZE = 1000
        const val PAGE_NUMBER = 0
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val SEND_MESSAGE_DESTINATION = "/app/chat.privateMessage"
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val PRIVATE_MESSAGES = "/private/messages"
        const val IMAGES_ENDPOINT = "/chat/image"
        const val IMAGES_FILES_PARAM = "image"
        const val CHAT_HISTORY_ENDPOINT = "/chat/history"
        const val CHAT_ID_PARAMETER = "chatId"

    }
}