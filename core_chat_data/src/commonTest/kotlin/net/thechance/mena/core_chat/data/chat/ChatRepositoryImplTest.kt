@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.core_chat.data.contacts.fakes.createCachedChatSummaryDto
import net.thechance.mena.core_chat.data.contacts.fakes.createChatDto
import net.thechance.mena.core_chat.data.contacts.fakes.createChatSummaryDto
import net.thechance.mena.core_chat.data.createChatRepository
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.defaultChatResponse
import net.thechance.mena.core_chat.data.defaultChatSummaryResponse
import net.thechance.mena.core_chat.data.jsonHeaders
import net.thechance.mena.core_chat.data.jsonSerialization
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.repository.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDao
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.model.SyncState
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
    private lateinit var cachedChatSummaryDao: CachedChatSummaryDao
    private lateinit var dataStore: DataStore<Preferences>
    private val authRepository = mock<AuthenticationRepository>()


    @BeforeTest
    fun setUp() {
        everySuspend { authRepository.getAccessToken() } returns "token"
        httpClient = createHttpClient()
        webSocketManager = mock<WebSocketManager>()
        messageDao = mock<MessageDao>()
        cachedChatSummaryDao = mock<CachedChatSummaryDao>()
        dataStore = mock<DataStore<Preferences>>()
        val emptyPrefs = emptyPreferences()
        everySuspend { dataStore.data } returns flowOf(emptyPrefs)
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
        )
    }

    @Test
    fun `should return chat when getChatByContactUserId is successful`() = runTest {
        httpClient = createHttpClient(chatResponse = { defaultChatResponse() })
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
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
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
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
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
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
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,

            )

        assertFailsWith<NotFoundException> {
            repository.getChatById(testChatId)
        }
    }

    @Test
    fun `should return chat summary when getChatsSummary is successful`() =
        runTest {
            httpClient = createHttpClient(
                chatsSummariesResponse = { mockErrorPagedResponse<ChatSummaryDto>(HttpStatusCode.NotFound) }
            )
            everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns listOf(createCachedChatSummaryDto())
            everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 1
            repository = createChatRepository(
                httpClient = httpClient,
                webSocketManager = webSocketManager,
                cachedChatSummaryDao = cachedChatSummaryDao,
                dataStore = dataStore,
            )

            assertThat(repository.getChatsSummary(0, 20).data).isNotEmpty()
        }

    @Test
    fun `should return empty list when getChatsSummary returns empty list from the room db`() =
        runTest {
            httpClient = createHttpClient(
                chatsSummariesResponse = { mockErrorPagedResponse<ChatSummaryDto>(HttpStatusCode.NotFound) }
            )
            everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns emptyList()
            everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0
            repository = createChatRepository(
                httpClient = httpClient,
                webSocketManager = webSocketManager,
                cachedChatSummaryDao = cachedChatSummaryDao,
                dataStore = dataStore,
            )

            assertThat(repository.getChatsSummary(0, 20).data).isEqualTo(emptyList())
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
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
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
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
        )

        assertFailsWith<NotFoundException> {
            repository.getChatSummaryById(testChatId)
        }
    }

    @Test
    fun `should throw Exception when deleteChatById returns 404`() = runTest {
        val testChatId = Uuid.random()

        httpClient = createHttpClient(
            deleteChatResponse = {
                respond("", HttpStatusCode.NotFound, jsonHeaders)
            }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
        )

        assertFailsWith<Exception> {
            repository.deleteChatById(testChatId)
        }
    }

    @Test
    fun `should throw Exception when deleteChatById returns server error`() = runTest {
        val testChatId = Uuid.random()

        httpClient = createHttpClient(
            deleteChatResponse = {
                respond("", HttpStatusCode.InternalServerError, jsonHeaders)
            }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
        )

        assertFailsWith<Exception> {
            repository.deleteChatById(testChatId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should emit error state when getChatsSummary sync fails in background`() = runTest {
        everySuspend { cachedChatSummaryDao.getChatSummaries(any(), any()) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0

        httpClient = createHttpClient(
            chatsSummariesResponse = { mockErrorPagedResponse<ChatSummaryDto>(HttpStatusCode.NotFound) }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore
        )

        val syncStates = mutableListOf<SyncState>()
        val job = launch {
            repository.observeChatSummariesSyncState().collect {
                syncStates.add(it)
            }
        }

        repository.getChatsSummary(pageNumber = 1, pageSize = 20)
        advanceUntilIdle()

        assertThat(syncStates.any { it is SyncState.Error }).isTrue()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should sync chat summaries in background and update cache`() = runTest {
        everySuspend { cachedChatSummaryDao.getChatSummaries(any(), any()) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0

        httpClient = createHttpClient(
            chatsSummariesResponse = { defaultChatSummaryResponse() }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore
        )

        repository.getChatsSummary(pageNumber = 1, pageSize = 20)
        advanceUntilIdle()

        verifySuspend { cachedChatSummaryDao.insertMultipleChatSummaries(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should emit ChatsSummariesSynced state when sync succeeds`() = runTest {
        everySuspend { cachedChatSummaryDao.getChatSummaries(any(), any()) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0

        httpClient = createHttpClient(
            chatsSummariesResponse = { defaultChatSummaryResponse() }
        )
        repository = createChatRepository(
            httpClient = httpClient,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore
        )

        val syncStates = mutableListOf<SyncState>()
        val job = launch {
            repository.observeChatSummariesSyncState().collect {
                syncStates.add(it)
            }
        }

        repository.getChatsSummary(pageNumber = 1, pageSize = 20)
        advanceUntilIdle()

        assertThat(syncStates.any { it is SyncState.ChatsSummariesSynced }).isTrue()
        job.cancel()
    }

    private companion object {
        private val userId = Uuid.random()
        const val IMAGE_URL = "http://test.com/image.jpg"
    }

}
