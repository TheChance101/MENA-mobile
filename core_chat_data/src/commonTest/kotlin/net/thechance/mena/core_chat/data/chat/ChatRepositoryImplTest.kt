@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.core_chat.data.chat.dto.MessageDto
import net.thechance.mena.core_chat.data.chat.utils.WebSocketManager
import net.thechance.mena.core_chat.data.contacts.createChatRepository
import net.thechance.mena.core_chat.data.contacts.createHttpClient
import net.thechance.mena.core_chat.data.contacts.defaultChatHistoryResponse
import net.thechance.mena.core_chat.data.contacts.defaultChatResponse
import net.thechance.mena.core_chat.data.contacts.fakes.createMessage
import net.thechance.mena.core_chat.data.contacts.jsonHeaders
import net.thechance.mena.core_chat.data.contacts.mockErrorPagedResponse
import net.thechance.mena.core_chat.domain.exception.NotFoundException
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
    private val authRepository = mock<AuthenticationRepository>()

    private val chatId = Uuid.random()
    private val userId = Uuid.random()

    @BeforeTest
    fun setUp() {
        everySuspend { authRepository.getAccessToken() } returns "token"
        httpClient = createHttpClient()
        webSocketManager = mock<WebSocketManager>()

        repository = createChatRepository(
            httpClient = httpClient,
            authenticationRepository = authRepository,
            webSocketManager = webSocketManager
        )
    }

    @Test
    fun `should return messages when loadMessages is successful`() = runTest {
        httpClient = createHttpClient(
            chatHistoryResponse = { defaultChatHistoryResponse() }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            authenticationRepository = authRepository,
            webSocketManager = webSocketManager
        )

        val result = repository.loadMessages(chatId)

        assertThat(result).isNotEmpty()
    }

    @Test
    fun `should throw NotFoundException when loadMessages returns error`() = runTest {
        httpClient = createHttpClient(
            chatHistoryResponse = { mockErrorPagedResponse<MessageDto>(HttpStatusCode.NotFound) }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            authenticationRepository = authRepository,
            webSocketManager = webSocketManager
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
            authenticationRepository = authRepository,
            webSocketManager = webSocketManager
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
            authenticationRepository = authRepository,
            webSocketManager = webSocketManager
        )

        assertFailsWith<NotFoundException> {
            repository.getChatByContactUserId(userId)
        }
    }

    @Test
    fun `should send message successfully when websocket is connected`() = runTest {
        every { webSocketManager.isConnected() } returns true
        everySuspend { webSocketManager.sendTextFrame(any(), any()) } returns Unit

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

}
