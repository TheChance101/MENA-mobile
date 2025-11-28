@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import net.thechance.mena.core_chat.data.contacts.fakes.createCachedChatSummaryDto
import net.thechance.mena.core_chat.data.contacts.fakes.createChatDto
import net.thechance.mena.core_chat.data.contacts.fakes.createChatSummaryDto
import net.thechance.mena.core_chat.data.createChatRepository
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.defaultChatResponse
import net.thechance.mena.core_chat.data.defaultDeleteChatResponse
import net.thechance.mena.core_chat.data.jsonHeaders
import net.thechance.mena.core_chat.data.jsonSerialization
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.mockSuccessPagedResponse
import net.thechance.mena.core_chat.data.repository.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageDao
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.domain.exception.NoInternetException
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class ChatRepositoryImplTest {

    private lateinit var httpClientHolder: HttpClientHolder
    private lateinit var repository: ChatRepositoryImpl
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var pendingMessagesDao: PendingMessageDao
    private lateinit var cachedChatSummaryDao: CachedChatSummaryDao
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var cachedChatDao: CachedChatDao
    private val authRepository = mock<AuthenticationRepository>()

    @BeforeTest
    fun setUp() {
        everySuspend { authRepository.getAccessToken() } returns "token"
        webSocketManager = mock<WebSocketManager>()
        pendingMessagesDao = mock<PendingMessageDao>()
        cachedChatSummaryDao = mock<CachedChatSummaryDao>()
        cachedChatDao = mock<CachedChatDao>()
        httpClientHolder = mock<HttpClientHolder>()
        dataStore = mock<DataStore<Preferences>>()

        val emptyPrefs = emptyPreferences()

        every { authRepository.observeTokenChange() } returns MutableStateFlow("fake_token")
        everySuspend { dataStore.data } returns flowOf(emptyPrefs)
        everySuspend { dataStore.updateData(any()) } returns emptyPreferences()

        everySuspend { cachedChatDao.getChatById(any()) } returns null
        everySuspend { cachedChatDao.insertChat(any()) } returns Unit
        everySuspend { cachedChatDao.insertAllChats(any()) } returns Unit
        everySuspend { cachedChatDao.deleteChatById(any()) } returns Unit
        everySuspend { cachedChatSummaryDao.clearAllChatSummaries() } returns Unit
        everySuspend { cachedChatDao.clearAllChats() } returns Unit
        everySuspend { webSocketManager.disconnect() } returns Unit
        every { httpClientHolder.getClient() } returns createHttpClient()
        every { httpClientHolder.reset() } returns Unit

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            cachedChatDao = cachedChatDao,
            dataStore = dataStore,
            authRepository = authRepository,
            cachedChatSummaryDao = cachedChatSummaryDao,
        )
    }

    @Test
    fun `should return chat when getChatByOtherUserId is successful`() = runTest {
        every { httpClientHolder.getClient() } returns createHttpClient(chatResponse = { defaultChatResponse() })

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val result = repository.getChatByOtherUserId(userId)

        assertThat(result.name).isEqualTo("Test Chat")
    }

    @Test
    fun `should throw ChatNotFoundException when getChatByOtherUserId fails`() = runTest {
        every { httpClientHolder.getClient() } returns createHttpClient(
            chatResponse = { respond("", HttpStatusCode.NotFound, jsonHeaders) }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            cachedChatDao = cachedChatDao,
            dataStore = dataStore,
            authRepository = authRepository,
            cachedChatSummaryDao = cachedChatSummaryDao,
        )

        assertFailsWith<NotFoundException> {
            repository.getChatByOtherUserId(userId)
        }
    }

    @Test
    fun `should return chat when getChatById is successful`() = runTest {
        val testChatId = Uuid.random()
        val chatDto = createChatDto(id = testChatId.toString(), name = "Chat By Id")

        every { httpClientHolder.getClient() } returns createHttpClient(
            chatByIdResponse = {
                respond(
                    content = jsonSerialization.encodeToString(ChatDto.serializer(), chatDto),
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders
                )
            }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val result = repository.getChatById(testChatId)

        assertThat(result.id).isEqualTo(testChatId)
        assertThat(result.name).isEqualTo("Chat By Id")
    }

    @Test
    fun `should throw NotFoundException when getChatById returns 404`() = runTest {
        val testChatId = Uuid.random()

        every { httpClientHolder.getClient() } returns createHttpClient(
            chatByIdResponse = {
                respond("", HttpStatusCode.NotFound, jsonHeaders)
            }
        )

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao

        )

        assertFailsWith<NotFoundException> {
            repository.getChatById(testChatId)
        }
    }

    @Test
    fun `should return chat summary when getChatsSummary is successful`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
                chatsSummariesResponse = { mockErrorPagedResponse<ChatSummaryDto>(HttpStatusCode.NotFound) }
            )
            everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns listOf(
                createCachedChatSummaryDto()
            )
            everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 1
            repository = createChatRepository(
                httpClientHolder = httpClientHolder,
                webSocketManager = webSocketManager,
                cachedChatSummaryDao = cachedChatSummaryDao,
                dataStore = dataStore,
                authRepository = authRepository,
                cachedChatDao = cachedChatDao
            )

            assertThat(repository.getChatsSummary(0, 20).data).isNotEmpty()
        }

    @Test
    fun `should return empty list when getChatsSummary returns empty list from the room db`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
                chatsSummariesResponse = { mockErrorPagedResponse<ChatSummaryDto>(HttpStatusCode.NotFound) }
            )
            everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns emptyList()
            everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0
            repository = createChatRepository(
                httpClientHolder = httpClientHolder,
                webSocketManager = webSocketManager,
                cachedChatSummaryDao = cachedChatSummaryDao,
                dataStore = dataStore,
                authRepository = authRepository,
                cachedChatDao = cachedChatDao
            )

            assertThat(repository.getChatsSummary(0, 20).data).isEqualTo(emptyList())
        }

    @Test
    fun `should throw NotFoundException when getChatSummaryById returns 404`() = runTest {
        val testChatId = Uuid.random()

        every { httpClientHolder.getClient() } returns createHttpClient(
            chatByIdResponse = { respond("", HttpStatusCode.NotFound, jsonHeaders) }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        assertFailsWith<NotFoundException> {
            repository.getChatSummaryById(testChatId)
        }
    }

    @Test
    fun `should throw Exception when deleteChatById returns 404`() = runTest {
        val testChatId = Uuid.random()

        every { httpClientHolder.getClient() } returns createHttpClient(
            deleteChatResponse = {
                respond("", HttpStatusCode.NotFound, jsonHeaders)
            }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        assertFailsWith<Exception> {
            repository.deleteChatById(testChatId)
        }
    }

    @Test
    fun `should throw Exception when deleteChatById returns server error`() = runTest {
        val testChatId = Uuid.random()

        every { httpClientHolder.getClient() } returns createHttpClient(
            deleteChatResponse = {
                respond("", HttpStatusCode.InternalServerError, jsonHeaders)
            }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            cachedChatSummaryDao = cachedChatSummaryDao,
            dataStore = dataStore,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        assertFailsWith<Exception> {
            repository.deleteChatById(testChatId)
        }
    }

    @Test
    fun `should sync chat data successfully and emit ChatsSummariesSynced`() = runTest {
        val pageNumber = 0
        val pageSize = 20
        val chatSummaries = listOf(
            createChatSummaryDto(id = Uuid.random().toString(), name = "Chat 1"),
            createChatSummaryDto(id = Uuid.random().toString(), name = "Chat 2")
        )
        val pagedData = PagedDataDto(
            data = chatSummaries,
            totalItems = 2,
            totalPages = 1,
            pageNumber = pageNumber,
            pageSize = pageSize
        )

        every { httpClientHolder.getClient() } returns createHttpClient(
            chatsSummariesResponse = {
                mockSuccessPagedResponse(pagedData)
            }
        )

        everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0
        everySuspend { cachedChatSummaryDao.insertMultipleChatSummaries(any()) } returns Unit

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val job = launch { repository.getChatsSummary(pageNumber, pageSize) }

        val emittedState = repository.observeChatSummariesSyncState()
            .first { it is SyncState.ChatsSummariesSynced }

        assertThat(emittedState).isEqualTo(SyncState.ChatsSummariesSynced(chatSummaries.map { it.toDomain()!! }))
        job.cancel()
    }

    @Test
    fun `should emit Offline when network is unavailable during chat sync`() = runTest {
        val pageNumber = 0
        val pageSize = 20

        every { httpClientHolder.getClient() } returns createHttpClient(
            chatsSummariesResponse = { throw NoInternetException() }
        )

        everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0
        everySuspend { cachedChatSummaryDao.insertMultipleChatSummaries(any()) } returns Unit

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val job = launch { repository.getChatsSummary(pageNumber, pageSize) }

        val emittedState =
            repository.observeChatSummariesSyncState().first { it == SyncState.Offline }

        assertThat(emittedState is SyncState.Offline).isTrue()
        job.cancel()
    }

    @Test
    fun `should emit Error when unknown exception occurs during chat sync`() = runTest {
        val pageNumber = 0
        val pageSize = 20

        every { httpClientHolder.getClient() } returns createHttpClient(
            chatsSummariesResponse = { throw Exception("Unexpected failure") }
        )

        everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0
        everySuspend { cachedChatSummaryDao.insertMultipleChatSummaries(any()) } returns Unit

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val job = launch { repository.getChatsSummary(pageNumber, pageSize) }

        val emittedState =
            repository.observeChatSummariesSyncState().first { it is SyncState.Error }

        assertThat(emittedState is SyncState.Error).isTrue()
        job.cancel()
    }

    @Test
    fun `should emit DeletedChatsSynced when lastSyncTime is not null`() = runTest {
        val pageNumber = 0
        val pageSize = 20
        val deletedIds = listOf(Uuid.random(), Uuid.random())

        val preferences = mutablePreferencesOf(
            stringPreferencesKey("lastTimeChatSummariesSynced") to Clock.System.now().toString()
        )

        everySuspend { dataStore.data } returns flowOf(preferences)
        everySuspend { cachedChatSummaryDao.getChatSummaries(20, 0) } returns emptyList()
        everySuspend { cachedChatSummaryDao.getChatSummariesCount() } returns 0
        everySuspend { cachedChatSummaryDao.insertMultipleChatSummaries(any()) } returns Unit
        everySuspend { cachedChatSummaryDao.deleteMultipleChatSummaries(any()) } returns Unit

        every { httpClientHolder.getClient() } returns createHttpClient(
            deleteChatResponse = {
                respond(
                    content = jsonSerialization.encodeToString(
                        ListSerializer(Uuid.serializer()),
                        deletedIds
                    ),
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders
                )
            },
            chatsSummariesResponse = {
                mockSuccessPagedResponse<PagedDataDto<ChatSummaryDto>>(
                    PagedDataDto(
                        data = emptyList(),
                        totalItems = 0,
                        totalPages = 1,
                        pageNumber = pageNumber,
                        pageSize = pageSize
                    )
                )
            }
        )

        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val job = launch { repository.getChatsSummary(pageNumber, pageSize) }

        val emittedState =
            repository.observeChatSummariesSyncState().first { it is SyncState.DeletedChatsSynced }

        assertThat(emittedState).isEqualTo(SyncState.DeletedChatsSynced(deletedIds))

        job.cancel()
    }


    @Test
    fun `should return deleted chats after specific time successfully`() = runTest {
        val deletedChats = listOf(Uuid.random(), Uuid.random())
        val testTime = Clock.System.now()

        every { httpClientHolder.getClient() } returns createHttpClient(
            deleteChatResponse = {
                defaultDeleteChatResponse()
                respond(
                    content = jsonSerialization.encodeToString(
                        ListSerializer(Uuid.serializer()),
                        deletedChats
                    ),
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders
                )
            }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        val result = repository.getDeletedChatAfterSpecificTime(testTime)

        assertThat(result).isEqualTo(deletedChats)
    }

    @Test
    fun `should throw NotFoundException when deleted chats not found`() = runTest {
        val testTime = Clock.System.now()

        every { httpClientHolder.getClient() } returns createHttpClient(
            deleteChatResponse = {
                respond("", HttpStatusCode.NotFound, jsonHeaders)
            }
        )
        repository = createChatRepository(
            httpClientHolder = httpClientHolder,
            webSocketManager = webSocketManager,
            dataStore = dataStore,
            cachedChatSummaryDao = cachedChatSummaryDao,
            authRepository = authRepository,
            cachedChatDao = cachedChatDao
        )

        assertFailsWith<NotFoundException> {
            repository.getDeletedChatAfterSpecificTime(testTime)
        }
    }

    private companion object {
        private val userId = Uuid.random()
    }

}
