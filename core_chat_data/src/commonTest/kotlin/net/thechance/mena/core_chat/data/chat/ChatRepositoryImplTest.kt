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
            imageDownloader = imageDownloader
        )
    }


    @Test
    fun `should return chat when getChatByContactUserId is successful`() = runTest {
        httpClient = createHttpClient(chatResponse = { defaultChatResponse() })
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
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
            imageDownloader = imageDownloader
        )

        assertFailsWith<NotFoundException> {
            repository.getChatSummaryById(testChatId)
        }
    }

    private companion object {
        private val userId = Uuid.random()
        const val IMAGE_URL = "http://test.com/image.jpg"
    }

}
