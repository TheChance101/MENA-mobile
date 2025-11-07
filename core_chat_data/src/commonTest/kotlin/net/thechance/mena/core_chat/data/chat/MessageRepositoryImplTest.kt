@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
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
import net.thechance.mena.core_chat.data.contacts.fakes.createMessage
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.createMessageRepository
import net.thechance.mena.core_chat.data.defaultAudioResponse
import net.thechance.mena.core_chat.data.defaultChatHistoryResponse
import net.thechance.mena.core_chat.data.defaultUploadImagesResponse
import net.thechance.mena.core_chat.data.jsonSerialization
import net.thechance.mena.core_chat.data.messagesender.AudioMessageSender
import net.thechance.mena.core_chat.data.messagesender.ImageMessageSender
import net.thechance.mena.core_chat.data.messagesender.MessageSenderFactory
import net.thechance.mena.core_chat.data.messagesender.TextMessageSender
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.repository.MessageRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
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
    private lateinit var audioMessageSender: AudioMessageSender
    private lateinit var messageDao: MessageDao

    @BeforeTest
    fun setUp() {
        httpClient = createHttpClient()
        webSocketManager = mock<WebSocketManager>()
        messageDao = mock<MessageDao>()

        textMessageSender = TextMessageSender(
            webSocketManager = webSocketManager,
            json = jsonSerialization
        )
        imageMessageSender = ImageMessageSender(client = httpClient)
        audioMessageSender = AudioMessageSender(client = httpClient)

        messageSenderFactory =
            MessageSenderFactory(textMessageSender, imageMessageSender, audioMessageSender)

        repository = createMessageRepository(
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            messageSenderFactory = messageSenderFactory,
            httpClient = httpClient
        )
    }

    @Test
    fun `should return messages when loadMessages is successful`() = runTest {
        httpClient = createHttpClient(
            chatHistoryResponse = { defaultChatHistoryResponse() }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            messageDao = messageDao,
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
        everySuspend { messageDao.deleteMessage(any()) } returns Unit

        repository.deleteMessage(message)

        verifySuspend { messageDao.deleteMessage(message.id.toString()) }
    }

    @Test
    fun `should throw NotFoundException when loadMessages returns error`() = runTest {
        httpClient = createHttpClient(
            chatHistoryResponse = { mockErrorPagedResponse<MessageDto>(HttpStatusCode.NotFound) }
        )
        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            messageDao = messageDao,
        )

        assertFailsWith<NotFoundException> {
            repository.loadMessages(chatId, 1, 40)
        }
    }

    @Test
    fun `should send message successfully when websocket is connected`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit
        everySuspend { messageDao.insertMessage(any()) } returns Unit
        everySuspend { messageDao.deleteMessage(any()) } returns Unit

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
        everySuspend { messageDao.insertMessage(any()) } returns Unit
        everySuspend { messageDao.updateMessageStatus(any(), any()) } returns Unit

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
                message1.toLocalDto(),
                message2.toLocalDto()
            )

            everySuspend { messageDao.getMessagesByChat(chatId.toString()) } returns flowOf(
                messageEntities
            )

            val result = repository.observePendingMessagesByChatId(chatId).first()


            assertThat(result).isNotEmpty()
            assertThat(result.size).isEqualTo(2)
            verifySuspend { messageDao.getMessagesByChat(chatId.toString()) }
        }

    @Test
    fun `should return empty list when no local messages exist for chat`() = runTest {
        everySuspend { messageDao.getMessagesByChat(chatId.toString()) } returns flowOf(emptyList())

        val result = repository.observePendingMessagesByChatId(chatId).first()

        assertThat(result.isEmpty()).isTrue()
        verifySuspend { messageDao.getMessagesByChat(chatId.toString()) }
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
            everySuspend { messageDao.insertMessage(any()) } returns Unit
            everySuspend { messageDao.deleteMessage(any()) } returns Unit

            httpClient = createHttpClient(
                imagesResponse = { defaultUploadImagesResponse() }
            )

            repository = createMessageRepository(
                httpClient = httpClient,
                webSocketManager = webSocketManager,
                messageSenderFactory = messageSenderFactory,
                messageDao = messageDao,
            )

            val byteArray = ByteArray(10)
            val message = createMessage(
                senderId = userId,
                chatId = chatId,
                content = MessageContent.Image(ImageData.ImageByteArray(byteArray))
            )

            repository.sendMessage(message)

            verifySuspend { messageDao.insertMessage(any()) }
            verifySuspend { messageDao.deleteMessage(any()) }
        }

    @Test
    fun `should mark message as FAILED when image upload throws exception`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { messageDao.insertMessage(any()) } returns Unit
        everySuspend { messageDao.updateMessageStatus(any(), any()) } returns Unit

        httpClient = createHttpClient(
            imagesResponse = { respondError(HttpStatusCode.InternalServerError) }
        )

        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            messageDao = messageDao,
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
            messageDao.updateMessageStatus(
                any(),
                MessageLocalDto.MessageStatus.FAILED
            )
        }
    }

    @Test
    fun `should send audio message successfully when websocket connected and audio uploaded`() =
        runTest {
            // Given
            every { webSocketManager.isConnected() } returns true
            everySuspend { messageDao.insertMessage(any()) } returns Unit
            everySuspend { messageDao.deleteMessage(any()) } returns Unit

            httpClient = createHttpClient(
                audioResponse = { defaultAudioResponse() }
            )

            repository = createMessageRepository(
                httpClient = httpClient,
                webSocketManager = webSocketManager,
                messageSenderFactory = messageSenderFactory,
                messageDao = messageDao,
            )

            val audioBytes = ByteArray(1024) { it.toByte() }
            val message = createMessage(
                senderId = userId,
                chatId = chatId,
                content = MessageContent.Audio(AudioData.AudioByteArray(audioBytes))
            )

            // When
            repository.sendMessage(message)

            // Then
            verifySuspend { messageDao.insertMessage(any()) }
            verifySuspend { messageDao.deleteMessage(any()) }
        }

    @Test
    fun `should mark message as FAILED when audio upload throws exception`() = runTest {
        // Given
        every { webSocketManager.isConnected() } returns true
        everySuspend { messageDao.insertMessage(any()) } returns Unit
        everySuspend { messageDao.updateMessageStatus(any(), any()) } returns Unit

        httpClient = createHttpClient(
            audioResponse = { respondError(HttpStatusCode.InternalServerError) }
        )

        repository = createMessageRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageSenderFactory = messageSenderFactory,
            messageDao = messageDao,
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
            messageDao.updateMessageStatus(
                any(),
                MessageLocalDto.MessageStatus.FAILED
            )
        }
    }

    private companion object {
        private val chatId = Uuid.random()
        private val userId = Uuid.random()
    }
}