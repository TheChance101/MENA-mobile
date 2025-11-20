@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.data.contacts.fakes.createMessage
import net.thechance.mena.core_chat.data.contacts.fakes.createMessageDto
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.createMessageRepository
import net.thechance.mena.core_chat.data.defaultAudioResponse
import net.thechance.mena.core_chat.data.defaultChatHistoryResponse
import net.thechance.mena.core_chat.data.defaultUploadImagesResponse
import net.thechance.mena.core_chat.data.jsonSerialization
import net.thechance.mena.core_chat.data.messagesender.AudioMessageSender
import net.thechance.mena.core_chat.data.messagesender.AyahMessageSender
import net.thechance.mena.core_chat.data.messagesender.ImageMessageSender
import net.thechance.mena.core_chat.data.messagesender.MessageSenderFactory
import net.thechance.mena.core_chat.data.messagesender.TextMessageSender
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.repository.MessageRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageDao
import net.thechance.mena.core_chat.data.source.local.database.chatSyncTime.ChatSyncTimeDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageDao
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toCachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.utils.now
import net.thechance.mena.core_chat.data.utils.toInstant
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.QuranService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MessageRepositoryImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var repository: MessageRepositoryImpl
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var messageSenderFactory: MessageSenderFactory
    private lateinit var textMessageSender: TextMessageSender
    private lateinit var imageMessageSender: ImageMessageSender
    private lateinit var pendingMessageDao: PendingMessageDao
    private lateinit var cachedMessageDao: CachedMessageDao
    private lateinit var chatSyncTimeDao: ChatSyncTimeDao
    private lateinit var audioMessageSender: AudioMessageSender
    private lateinit var quranRepository: QuranRepository
    private lateinit var quranService: QuranService
    private lateinit var ayahMessageSender: AyahMessageSender

    @BeforeTest
    fun setUp() {
        httpClient = createHttpClient()
        webSocketManager = mock<WebSocketManager>()
        pendingMessageDao = mock<PendingMessageDao>()
        chatSyncTimeDao = mock<ChatSyncTimeDao>()
        cachedMessageDao = mock<CachedMessageDao>()
        quranRepository =mock<QuranRepository>()
        quranService = QuranService(repository = quranRepository)
        textMessageSender = TextMessageSender(
            webSocketManager = webSocketManager,
            json = jsonSerialization
        )
        imageMessageSender = ImageMessageSender(client = httpClient)
        audioMessageSender = AudioMessageSender(client = httpClient)
        ayahMessageSender =
            AyahMessageSender(webSocketManager = webSocketManager, json = jsonSerialization)
        messageSenderFactory =
            MessageSenderFactory(
                textMessageSender,
                imageMessageSender,
                audioMessageSender,
                ayahMessageSender
            )

        repository = createMessageRepository(
            webSocketManager = webSocketManager,
            pendingMessageDao = pendingMessageDao,
            messageSenderFactory = messageSenderFactory,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            httpClient = httpClient,
            quranService = quranService
        )
    }


    @Test
    fun `should return messages when loadMessages is successful`() = runTest {
        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns listOf(createMessage().toCachedMessageLocalDto())
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns null
        everySuspend { chatSyncTimeDao.upsert(any()) } returns Unit
        everySuspend { cachedMessageDao.getTotalMessagesCount(any()) } returns 1
        httpClient = createHttpClient(
            chatHistoryResponse = { defaultChatHistoryResponse() }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )

        val result = repository.loadMessages(chatId, 1, 40)

        assertThat(result.data).isNotEmpty()
    }


    @Test
    fun `should delete message from local database when deleteMessage is called`() = runTest {
        val message = createMessage(
            senderId = userId,
            chatId = chatId,
        )
        everySuspend { pendingMessageDao.deleteMessageById(any()) } returns Unit

        repository.deleteMessageById(message.id)

        verifySuspend { pendingMessageDao.deleteMessageById(message.id.toString()) }
    }


    @Test
    fun `should throw NotFoundException when loadMessages returns error`() = runTest {
        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns emptyList()
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns null
        httpClient = createHttpClient(
            chatHistoryResponse = { mockErrorPagedResponse<MessageDto>(HttpStatusCode.NotFound) }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )

        assertFailsWith<NotFoundException> {
            repository.loadMessages(chatId, 1, 40)
        }
    }

    @Test
    fun `should send message successfully when websocket is connected`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit
        everySuspend { pendingMessageDao.insertMessage(any()) } returns Unit
        everySuspend { pendingMessageDao.deleteMessageById(any()) } returns Unit

        val message = createMessage(
            senderId = userId,
            chatId = chatId,
        )

        repository.sendMessage(message)

        verifySuspend {
            webSocketManager.sendTextFrame(
                destination = "/app/chat.privateMessage",
                payload = any()
            )
        }
    }

    @Test
    fun `should throw SendMessageFailedException when websocket is not connected`() = runTest {
        every { webSocketManager.isConnected() } returns false
        everySuspend { pendingMessageDao.insertMessage(any()) } returns Unit
        everySuspend { pendingMessageDao.updateMessageStatus(any(), any()) } returns Unit

        val message = createMessage(
            senderId = userId,
            chatId = chatId,
        )

        assertFailsWith<SendMessageFailedException> {
            repository.sendMessage(message)
        }
    }

    @Test
    fun `should observe read messages`() = runTest {
        val flow = repository.observeReadMessages()
        assertThat(flow).isNotNull()
    }

    @Test
    fun `should observe delete chat event`() = runTest {
        val flow = repository.observeDeleteChat()
        assertThat(flow).isNotNull()
    }

    @Test
    fun `should observe add message reaction event`() = runTest {
        val flow = repository.observeMessageReactions()
        assertThat(flow).isNotNull()
    }

    @Test
    fun `should observe remove message reaction event`() = runTest {
        val flow = repository.observeRemovedMessageReactions()
        assertThat(flow).isNotNull()
    }

    @Test
    fun `should return local messages from database when observePendingMessagesByChatId is called`() =
        runTest {
            val message1 = createMessage(senderId = userId, chatId = chatId)
            val message2 = createMessage(senderId = userId, chatId = chatId)
            val messageEntities = listOf(
                message1.toPendingMessageLocalDto(),
                message2.toPendingMessageLocalDto()
            )

            everySuspend { pendingMessageDao.getMessagesByChatId(chatId.toString()) } returns flowOf(
                messageEntities
            )

            val result = repository.observePendingMessagesByChatId(chatId).first()


            assertThat(result).isNotEmpty()
            assertThat(result.size).isEqualTo(2)
            verifySuspend { pendingMessageDao.getMessagesByChatId(chatId.toString()) }
        }

    @Test
    fun `should return empty list when no local messages exist for chat`() = runTest {
        everySuspend { pendingMessageDao.getMessagesByChatId(chatId.toString()) } returns flowOf(
            emptyList()
        )

        val result = repository.observePendingMessagesByChatId(chatId).first()

        assertThat(result.isEmpty()).isTrue()
        verifySuspend { pendingMessageDao.getMessagesByChatId(chatId.toString()) }
    }

    @Test
    fun `should return flow when observeMessagesForChatOrAll is called and websocket is connected`() =
        runTest {
            every { webSocketManager.isConnected() } returns true
            everySuspend { webSocketManager.connect(any()) } returns Unit
            everySuspend { webSocketManager.subscribe(any()) } returns Unit
            everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit
            every { webSocketManager.incomingMessages } returns MutableSharedFlow<String>().apply {
                tryEmit(
                    "test-message"
                )
            }

            val flow = repository.observeMessagesForChatOrAll(chatId)

            assertThat(flow).isNotNull()
        }

    @Test
    fun `should send image message successfully when websocket connected and images uploaded`() =
        runTest {
            every { webSocketManager.isConnected() } returns true
            everySuspend { pendingMessageDao.insertMessage(any()) } returns Unit
            everySuspend { pendingMessageDao.deleteMessageById(any()) } returns Unit

            httpClient = createHttpClient(
                imagesResponse = { defaultUploadImagesResponse() }
            )

            repository = createMessageRepository(
                httpClient = httpClient,
                webSocketManager = webSocketManager,
                messageSenderFactory = messageSenderFactory,
                pendingMessageDao = pendingMessageDao,
                cachedMessageDao = cachedMessageDao,
                chatSyncTimeDao = chatSyncTimeDao,
                quranService = quranService
            )

            val byteArray = ByteArray(10)
            val message = createMessage(
                senderId = userId,
                chatId = chatId,
                content = MessageContent.Image(ImageData.ImageByteArray(byteArray))
            )

            repository.sendMessage(message)

            verifySuspend { pendingMessageDao.insertMessage(any()) }
            verifySuspend { pendingMessageDao.deleteMessageById(any()) }
        }

    @Test
    fun `should mark message as FAILED when image upload throws exception`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { pendingMessageDao.insertMessage(any()) } returns Unit
        everySuspend { pendingMessageDao.updateMessageStatus(any(), any()) } returns Unit

        httpClient = createHttpClient(
            imagesResponse = { respondError(HttpStatusCode.InternalServerError) }
        )

        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )

        val byteArray = ByteArray(10)
        val message = createMessage(
            senderId = userId,
            chatId = chatId,
            content = MessageContent.Image(ImageData.ImageByteArray(byteArray))
        )

        assertFailsWith<SendMessageFailedException> {
            repository.sendMessage(message)
        }

        verifySuspend {
            pendingMessageDao.updateMessageStatus(
                any(),
                MessageStatus.FAILED
            )
        }
    }

    @Test
    fun `should send audio message successfully when websocket connected and audio uploaded`() =
        runTest {
            every { webSocketManager.isConnected() } returns true
            everySuspend { pendingMessageDao.insertMessage(any()) } returns Unit
            everySuspend { pendingMessageDao.deleteMessageById(any()) } returns Unit

            httpClient = createHttpClient(
                audioResponse = { defaultAudioResponse() }
            )

            repository = createMessageRepository(
                httpClient = httpClient,
                webSocketManager = webSocketManager,
                messageSenderFactory = messageSenderFactory,
                pendingMessageDao = pendingMessageDao,
                cachedMessageDao = cachedMessageDao,
                chatSyncTimeDao = chatSyncTimeDao,
                quranService = quranService
            )

            val audioBytes = ByteArray(1024) { it.toByte() }
            val message = createMessage(
                senderId = userId,
                chatId = chatId,
                content = MessageContent.Audio(AudioData.AudioByteArray(audioBytes))
            )

            repository.sendMessage(message)

            verifySuspend { pendingMessageDao.insertMessage(any()) }
            verifySuspend { pendingMessageDao.deleteMessageById(any()) }
        }

    @Test
    fun `should mark message as FAILED when audio upload throws exception`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { pendingMessageDao.insertMessage(any()) } returns Unit
        everySuspend { pendingMessageDao.updateMessageStatus(any(), any()) } returns Unit

        httpClient = createHttpClient(
            audioResponse = { respondError(HttpStatusCode.InternalServerError) }
        )

        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )

        val audioBytes = ByteArray(1024) { it.toByte() }
        val message = createMessage(
            senderId = userId,
            chatId = chatId,
            content = MessageContent.Audio(AudioData.AudioByteArray(audioBytes))
        )

        // When & Then
        assertFailsWith<SendMessageFailedException> {
            repository.sendMessage(message)
        }

        verifySuspend {
            pendingMessageDao.updateMessageStatus(
                any(),
                MessageStatus.FAILED
            )
        }
    }


    @Test
    fun `should throw NotFoundException when remote returns null`() = runTest {
        httpClient = createHttpClient(
            chatHistoryResponse = { mockErrorPagedResponse<MessageDto>(HttpStatusCode.NotFound) }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )

        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns emptyList()
        everySuspend { cachedMessageDao.getTotalMessagesCount(any()) } returns 0
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns null

        assertFailsWith<NotFoundException> {
            repository.loadMessages(chatId, 0, 20)
        }
    }


    @Test
    fun `should sync after last update when lastSyncTime exists`() = runTest {
        val cachedMessage = createMessage().toCachedMessageLocalDto()
        val now = LocalDateTime.now().toInstant().toString()
        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns listOf(cachedMessage)
        everySuspend { cachedMessageDao.getTotalMessagesCount(any()) } returns 1
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns now
        everySuspend { cachedMessageDao.insertAllMessages(any()) } returns Unit
        everySuspend { chatSyncTimeDao.upsert(any()) } returns Unit
        httpClient = createHttpClient(
            syncLatestMessagesResponse = { defaultChatHistoryResponse() }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )
        repository.loadMessages(chatId, 0, 10)
        verifySuspend { cachedMessageDao.insertAllMessages(any()) }
        verifySuspend { chatSyncTimeDao.upsert(any()) }
    }

    @Test
    fun `should insert messages and update sync time in syncAfterLastUpdate`() = runTest {
        val now = LocalDateTime.now().toInstant().toString()
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns now
        everySuspend { chatSyncTimeDao.upsert(any()) } returns Unit
        everySuspend { cachedMessageDao.insertAllMessages(any()) } returns Unit
        httpClient = createHttpClient(
            syncLatestMessagesResponse = { defaultChatHistoryResponse() }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )
        repository.syncAfterLastUpdate(chatId)
        verifySuspend { cachedMessageDao.insertAllMessages(any()) }
        verifySuspend { chatSyncTimeDao.upsert(any()) }
    }

    @Test
    fun `should fetch messages from remote when cache is empty and insert them`() = runTest {
        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns emptyList()
        everySuspend { cachedMessageDao.getTotalMessagesCount(any()) } returns 0
        everySuspend { cachedMessageDao.insertAllMessages(any()) } returns Unit
        everySuspend { pendingMessageDao.deleteMessagesByIds(any()) } returns Unit
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns null
        everySuspend { chatSyncTimeDao.upsert(any()) } returns Unit
        httpClient = createHttpClient(
            chatHistoryResponse = { defaultChatHistoryResponse() }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )
        val result = repository.loadMessages(chatId, 0, 20)

        assertThat(result.data).isNotEmpty()
        assertThat(result.totalItems).isGreaterThan(0)
        verifySuspend { cachedMessageDao.insertAllMessages(any()) }
    }

    @Test
    fun `should upsert sync time when lastSyncTime is null in loadMessages`() = runTest {
        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns listOf(createMessage().toCachedMessageLocalDto())
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns null
        everySuspend { chatSyncTimeDao.upsert(any()) } returns Unit
        everySuspend { cachedMessageDao.getTotalMessagesCount(any()) } returns 1

        val result = repository.loadMessages(chatId, 1, 40)

        assertThat(result.data).isNotEmpty()
        verifySuspend { chatSyncTimeDao.upsert(any()) }
    }

    @Test
    fun `should throw NotFoundException when getFromRemote returns null response`() = runTest {
        everySuspend {
            cachedMessageDao.getMessagesByChatIdWithOffset(
                any(),
                any(),
                any()
            )
        } returns emptyList()
        everySuspend { chatSyncTimeDao.getLastSyncTime(any()) } returns null

        httpClient = createHttpClient(
            chatHistoryResponse = { mockErrorPagedResponse<MessageDto>(HttpStatusCode.NotFound) }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            pendingMessageDao = pendingMessageDao,
            cachedMessageDao = cachedMessageDao,
            chatSyncTimeDao = chatSyncTimeDao,
            quranService = quranService
        )

        assertFailsWith<NotFoundException> {
            repository.loadMessages(chatId, 0, 20)
        }
    }

    @Test
    fun `should filter messages by chatId in observeMessagesForChatOrAll`() = runTest {
        every { webSocketManager.isConnected() } returns true

        val flow = repository.observeMessagesForChatOrAll(chatId)

        everySuspend { webSocketManager.incomingMessages } returns MutableSharedFlow<String>().apply {
            tryEmit(
                createMockIncomingMessage(
                    PRIVATE_MESSAGES,
                    jsonSerialization.encodeToString(
                        MessageDto.serializer(),
                        createMessageDto(chatId = chatId.toString())
                    )
                )
            )
        }

        assertThat(flow).isNotNull()
    }

    @Test
    fun `should send mark as read frame in markMessagesOfChatAsRead`() = runTest {
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit

        repository.markMessagesOfChatAsRead(chatId)

        verifySuspend {
            webSocketManager.sendTextFrame(
                destination = MARK_AS_READ_DESTINATION,
                payload = any()
            )
        }
    }

    @Test
    fun `should throw SendMessageFailedException when websocket not connected in sendMessageReactionEvent via addMessageReaction`() =
        runTest {
            every { webSocketManager.isConnected() } returns false

            assertFailsWith<SendMessageFailedException> {
                repository.addMessageReaction(Uuid.random(), "👍")
            }
        }

    @Test
    fun `should send add reaction frame in addMessageReaction when connected`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit

        val messageId = Uuid.random()
        val emoji = "👍"

        repository.addMessageReaction(messageId, emoji)

        verifySuspend {
            webSocketManager.sendTextFrame(
                destination = ADD_REACTION_DESTINATION,
                payload = any()
            )
        }
    }

    @Test
    fun `should send remove reaction frame in removeMessageReaction when connected`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit

        val messageId = Uuid.random()
        val emoji = "👍"

        repository.removeMessageReaction(messageId, emoji)

        verifySuspend {
            webSocketManager.sendTextFrame(
                destination = REMOVE_REACTION_DESTINATION,
                payload = any()
            )
        }
    }

    private fun createMockIncomingMessage(destination: String, body: String): String {
        val headers = "destination:$WEB_SOCKETS_USER_DESTINATION_PREFIX$destination\n\n"
        return "$headers$body"
    }

    private companion object {
        const val MARK_AS_READ_DESTINATION = "/app/chat.markAsRead"
        const val WEB_SOCKETS_USER_DESTINATION_PREFIX = "/user"
        const val PRIVATE_MESSAGES = "/private/messages"
        const val ADD_REACTION_DESTINATION = "/app/chat.addMessageReaction"
        const val REMOVE_REACTION_DESTINATION = "/app/chat.deleteMessageReaction"
        private val chatId = Uuid.random()
        private val userId = Uuid.random()
    }
}
