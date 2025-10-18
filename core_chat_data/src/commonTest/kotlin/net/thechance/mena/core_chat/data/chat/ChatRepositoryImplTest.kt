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
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.thechance.mena.core_chat.data.createChatRepository
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.defaultChatHistoryResponse
import net.thechance.mena.core_chat.data.defaultChatResponse
import net.thechance.mena.core_chat.data.defaultChatSummaryResponse
import net.thechance.mena.core_chat.data.contacts.fakes.createChatDto
import net.thechance.mena.core_chat.data.contacts.fakes.createChatSummaryDto
import net.thechance.mena.core_chat.data.contacts.fakes.createMessage
import net.thechance.mena.core_chat.data.defaultUploadImagesResponse
import net.thechance.mena.core_chat.data.jsonHeaders
import net.thechance.mena.core_chat.data.jsonSerialization
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.repository.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.network.ImageDownloader
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.OperationFailedException
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class ChatRepositoryImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var repository: ChatRepositoryImpl
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var messageDao: MessageDao
    private lateinit var imageDownloader: ImageDownloader
    private val authRepository = mock<AuthenticationRepository>()


    @BeforeTest
    fun setUp() {
        everySuspend { authRepository.getAccessToken() } returns "token"
        httpClient = createHttpClient()
        webSocketManager = mock<WebSocketManager>()
        messageDao = mock<MessageDao>()
        imageDownloader = mock<ImageDownloader>()

        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )
    }

    @Test
    fun `should return messages when loadMessages is successful`() = runTest {
        httpClient = createHttpClient(
            chatHistoryResponse = { defaultChatHistoryResponse() }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val result = repository.loadMessages(chatId)

        assertThat(result).isNotEmpty()
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
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        assertFailsWith<NotFoundException> {
            repository.loadMessages(chatId)
        }
    }


    @Test
    fun `should return chat when getChatByContactUserId is successful`() = runTest {
        httpClient = createHttpClient(chatResponse = { defaultChatResponse() })
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val result = repository.getChatByContactUserId(userId)

        assertThat(result.name).isEqualTo("Test Chat")
    }

    @Test
    fun `should throw ChatNotFoundException when getChatByContactUserId fails`() = runTest {
        httpClient = createHttpClient(
            chatResponse = { respond("", HttpStatusCode.NotFound, jsonHeaders) }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        assertFailsWith<NotFoundException> {
            repository.getChatByContactUserId(userId)
        }
    }

    @Test
    fun `should return chat when getChatById is successful`() = runTest {
        val testChatId = Uuid.random()
        val chatDto = createChatDto(id = testChatId.toString(), name = "Chat By Id")

        httpClient = createHttpClient(
            chatByIdResponse = {
                respond(
                    content = jsonSerialization.encodeToString(ChatDto.serializer(), chatDto),
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders
                )
            }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val result = repository.getChatById(testChatId)

        assertThat(result.id).isEqualTo(testChatId)
        assertThat(result.name).isEqualTo("Chat By Id")
    }

    @Test
    fun `should throw NotFoundException when getChatById returns 404`() = runTest {
        val testChatId = Uuid.random()

        httpClient = createHttpClient(
            chatByIdResponse = {
                respond("", HttpStatusCode.NotFound, jsonHeaders)
            }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        assertFailsWith<NotFoundException> {
            repository.getChatById(testChatId)
        }
    }

    @Test
    fun `should return chat summary when getChatsSummary is successful`() = runTest {
        httpClient = createHttpClient(
            chatSummaryResponse = { defaultChatSummaryResponse() }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val result = repository.getChatsSummary(
            pageNumber = 1,
            pageSize = 20
        )
        assertThat(result.data).isNotEmpty()
    }

    @Test
    fun `should throw NotFoundException when getChatsSummary returns error`() = runTest {
        httpClient = createHttpClient(
            chatSummaryResponse = { mockErrorPagedResponse<ChatSummaryDto>(HttpStatusCode.NotFound) }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        assertFailsWith<NotFoundException> {
            repository.getChatsSummary(
                pageNumber = 1,
                pageSize = 20
            )
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
    fun `should disconnect websocket when disconnect is called`() = runTest {
        everySuspend { webSocketManager.disconnect() } returns Unit
        repository.disconnect()
        verifySuspend { webSocketManager.disconnect() }
    }

    @Test
    fun `should observe read messages`() = runTest {
        val flow = repository.observeReadMessages()
        assertThat(flow).isNotNull()
    }

    @Test
    fun `should return local messages from database when getLocalMessages is called`() = runTest {
        val message1 = createMessage(senderId = userId, chatId = chatId)
        val message2 = createMessage(senderId = userId, chatId = chatId)
        val messageEntities = listOf(
            message1.toLocalDto(),
            message2.toLocalDto()
        )

        everySuspend { messageDao.getMessagesByChat(chatId.toString()) } returns flowOf(messageEntities)

        val result = repository.getLocalMessages(chatId).first()


        assertThat(result).isNotEmpty()
        assertThat(result.size).isEqualTo(2)
        verifySuspend { messageDao.getMessagesByChat(chatId.toString()) }
    }

    @Test
    fun `should return empty list when no local messages exist for chat`() = runTest {
        everySuspend { messageDao.getMessagesByChat(chatId.toString()) } returns flowOf(emptyList())

        val result = repository.getLocalMessages(chatId).first()

        assertThat(result.isEmpty()).isTrue()
        verifySuspend { messageDao.getMessagesByChat(chatId.toString()) }
    }

    @Test
    fun `should return flow when getMessages is called and websocket is connected`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { authRepository.getAccessToken() } returns "test-token"
        everySuspend { webSocketManager.connect(any()) } returns Unit
        everySuspend { webSocketManager.subscribe(any()) } returns Unit
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit
        every { webSocketManager.incomingMessages } returns MutableSharedFlow<String>().apply {
            tryEmit(
                "test-message"
            )
        }

        val flow = repository.getMessages(chatId)

        assertThat(flow).isNotNull()
    }

    @Test
    fun `downloadImage should call imageDownloader and run successfully when downloadImageToGallery return true`() =
        runTest {
            everySuspend { imageDownloader.downloadImageToGallery(any()) } returns true

            repository.downloadImage(IMAGE_URL)

            verifySuspend { imageDownloader.downloadImageToGallery(IMAGE_URL) }
        }

    @Test
    fun `downloadImage should throw OperationFailedException when downloadImage return false`() =
        runTest {
            everySuspend { imageDownloader.downloadImageToGallery(any()) } returns false

            assertFailsWith<OperationFailedException> {
                repository.downloadImage(IMAGE_URL)
            }
        }

    @Test
    fun `should return chat summary when getChatSummaryById is successful`() = runTest {
        val testChatId = Uuid.random()
        val dto = createChatSummaryDto(id = testChatId.toString(), name = "Summary Chat")

        httpClient = createHttpClient(
            chatByIdResponse = {
                respond(
                    content = jsonSerialization.encodeToString(ChatSummaryDto.serializer(), dto),
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders
                )
            }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val result = repository.getChatSummaryById(testChatId)

        assertThat(result.id).isEqualTo(testChatId)
        assertThat(result.name).isEqualTo("Summary Chat")
    }

    @Test
    fun `should throw NotFoundException when getChatSummaryById returns 404`() = runTest {
        val testChatId = Uuid.random()

        httpClient = createHttpClient(
            chatByIdResponse = { respond("", HttpStatusCode.NotFound, jsonHeaders) }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        assertFailsWith<NotFoundException> {
            repository.getChatSummaryById(testChatId)
        }
    }

    @Test
    fun `should send image message successfully when websocket connected and images uploaded`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { messageDao.insertMessage(any()) } returns Unit
        everySuspend { messageDao.updateMessageImages(any(), any()) } returns Unit
        everySuspend { messageDao.deleteMessage(any()) } returns Unit

        httpClient = createHttpClient(
            imagesResponse = { defaultUploadImagesResponse() }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val byteArrays = listOf(ByteArray(10), ByteArray(20))
        val message = createMessage(
            senderId = userId,
            chatId = chatId,
            content = MessageContent.Images(ImagesSource.Local(byteArrays))
        )

        repository.sendMessage(message)

        verifySuspend { messageDao.insertMessage(any()) }
        verifySuspend { messageDao.updateMessageImages(any(), any()) }
        verifySuspend { messageDao.deleteMessage(any()) }
    }


    @Test
    fun `should mark message as FAILED when image upload throws exception`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { messageDao.insertMessage(any()) } returns Unit
        everySuspend { messageDao.updateMessageStatus(any(), any()) } returns Unit

        // Simulate failed upload
        httpClient = createHttpClient(
            imagesResponse = { respondError(HttpStatusCode.InternalServerError) }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            messageDao = messageDao,
            imageDownloader = imageDownloader
        )

        val byteArrays = listOf(ByteArray(10))
        val message = createMessage(
            senderId = userId,
            chatId = chatId,
            content = MessageContent.Images(ImagesSource.Local(byteArrays))
        )

        assertFailsWith<SendMessageFailedException> {
            repository.sendMessage(message)
        }

        verifySuspend { messageDao.updateMessageStatus(any(), MessageLocalDto.MessageStatus.FAILED) }
    }


    private companion object {
        private val chatId = Uuid.random()
        private val userId = Uuid.random()

        const val IMAGE_URL = "http://test.com/image.jpg"
    }

}
