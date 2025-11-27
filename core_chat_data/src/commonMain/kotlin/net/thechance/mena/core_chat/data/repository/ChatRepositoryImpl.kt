package net.thechance.mena.core_chat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.toCached
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.toDomain
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfChatSummary
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.exception.NoInternetException
import net.thechance.mena.core_chat.domain.exception.OperationFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val clientHolder: HttpClientHolder,
    private val webSocketManager: WebSocketManager,
    private val cachedChatDao: CachedChatDao,
    private val cachedChatSummaryDao: CachedChatSummaryDao,
    private val authRepository: AuthenticationRepository,
    private val dataStore: DataStore<Preferences>
) : ChatRepository {

    private val _syncState = MutableSharedFlow<SyncState>()
    private val client: HttpClient
        get() = clientHolder.getClient()
    override fun observeChatSummariesSyncState(): Flow<SyncState> {
        return _syncState
    }

    init {
        observeAuthenticationState()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getChatsSummary(pageNumber: Int, pageSize: Int): PagedData<ChatSummary> {
        syncChatSummaries(pageNumber, pageSize)
        val cachedData = cachedChatSummaryDao.getChatSummaries(
            pageSize = pageSize,
            offset = pageNumber * pageSize
        ).map { it.toDomain() }

        val totalItems = cachedChatSummaryDao.getChatSummariesCount()
        val isLastPage = cachedData.size < pageSize
        val result = PagedData(data = cachedData, totalItems = totalItems, isLastPage = isLastPage)

        return result
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun syncChatSummaries(pageNumber: Int, pageSize: Int) {
        val lastSyncTime: Instant? = getLastTimeSynced()
        if (lastSyncTime != null) syncDeletedChats(lastSyncTime)

        handleSyncingDataSafely {
            val remoteChatSummaries = tryNetworkCall<PagedDataDto<ChatSummaryDto>>(
                bodyType = typeInfo<PagedDataDto<ChatSummaryDto>>()
            ) {
                client.get(CHATS_SUMMARIES_ENDPOINT) {
                    parameter(PAGE_NUMBER_PARAMETER, pageNumber)
                    parameter(PAGE_SIZE_PARAMETER, pageSize)
                }
            }.toPagedListOfChatSummary()

            val remoteChatSummariesData = remoteChatSummaries.data
            if (remoteChatSummariesData.isNotEmpty()) {
                cachedChatSummaryDao.insertMultipleChatSummaries(remoteChatSummariesData.map {
                    it.toCached()
                })
            }
            _syncState.emit(SyncState.ChatsSummariesSynced(remoteChatSummariesData))
        }

        updateSyncedTime()
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun updateSyncedTime() {
        dataStore.edit { preferences ->
            preferences[LAST_TIME_CHAT_SUMMARIES_SYNCED_KEY] = Clock.System.now().toString()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun getLastTimeSynced(): Instant? {
        val lastTimeSynced: String? = dataStore.data.map {
            it[LAST_TIME_CHAT_SUMMARIES_SYNCED_KEY]
        }.firstOrNull()
        return if (lastTimeSynced.isNullOrEmpty()) null else Instant.parse(lastTimeSynced)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun syncDeletedChats(time: Instant) {
        handleSyncingDataSafely {
            val deletedChatsId = getDeletedChatAfterSpecificTime(time)
            cachedChatSummaryDao.deleteMultipleChatSummaries(deletedChatsId.map { it.toString() })
            _syncState.emit(SyncState.DeletedChatsSynced(deletedChatsId))
        }
    }

    override suspend fun getChatSummaryById(chatId: Uuid): ChatSummary {
        return tryNetworkCall<ChatSummaryDto>(
            bodyType = typeInfo<ChatSummaryDto>()
        ) {
            client.get(getChatSummaryEndpoint(chatId))
        }.toDomain()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getDeletedChatAfterSpecificTime(time: Instant): List<Uuid> {
        return tryNetworkCall(
            bodyType = typeInfo<List<Uuid>>()
        ) {
            client.get(DELETED_CHATS_ENDPOINT) {
                parameter(DELETED_AFTER_PARAMETER, time)
            }
        }
    }

    private suspend fun handleSyncingDataSafely(
        callee: suspend () -> Unit
    ) {
        try {
            callee()
        } catch (_: NoInternetException) {
            _syncState.emit(SyncState.Offline)
        } catch (e: Exception) {
            _syncState.emit(SyncState.Error(e))
        }
    }

    override suspend fun getChatByOtherUserId(userId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(
            bodyType = typeInfo<ChatDto>()
        ) {
            client.get(CHAT_ENDPOINT) {
                parameter(RECEIVER_ID_PARAMETER, userId)
            }
        }.toDomain()
    }

    override suspend fun deleteChatById(chatId: Uuid) {
        tryNetworkCall<Unit>(
            bodyType = typeInfo<Unit>(),
            defaultException = OperationFailedException("failed to delete message from data")
        ) {
            client.delete("$CHAT_ENDPOINT/$chatId")
        }.also {
            cachedChatDao.deleteChatById(chatId.toString())
        }
        cachedChatSummaryDao.deleteChatSummaryById(chatId.toString())
    }

    override suspend fun getChatById(chatId: Uuid): Chat {
        return cachedChatDao.getChatById(chatId.toString())?.toDomain()
            ?: tryNetworkCall<ChatDto>(bodyType = typeInfo<ChatDto>()) {
                client.get("$CHAT_ENDPOINT/$chatId")
            }
                .also { chat -> cachedChatDao.insertChat(chat.toLocalDto()) }
                .toDomain()
    }


    override suspend fun disconnect() {
        webSocketManager.disconnect()
    }

    private fun observeAuthenticationState() {
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.observeTokenChange().collectLatest { token ->
                if (token.isEmpty()) {
                    clearAllChatCache()
                }else{
                    clientHolder.reset()
                }
            }
        }
    }

    private suspend fun clearAllChatCache() {
        try {
            webSocketManager.disconnect()
            cachedChatDao.clearAllChats()
            cachedChatSummaryDao.clearAllChatSummaries()
            dataStore.edit { preferences ->
                preferences.remove(LAST_TIME_CHAT_SUMMARIES_SYNCED_KEY)
            }

        } catch (e: Exception) {
            println("ChatRepository ERROR: Failed to clear cache. Error: ${e.message}")
        }
    }

    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val RECEIVER_ID_PARAMETER = "receiverId"
        const val DELETED_AFTER_PARAMETER = "deletedAfter"
        const val CHAT_ENDPOINT = "/chat"
        const val CHATS_SUMMARIES_ENDPOINT = "/chat/chatsSummary"
        const val DELETED_CHATS_ENDPOINT = "/chat/deletedChats"

        val LAST_TIME_CHAT_SUMMARIES_SYNCED_KEY =
            stringPreferencesKey("lastTimeChatSummariesSynced")

        fun getChatSummaryEndpoint(chatId: Uuid): String {
            return "/chat/${chatId}/summary"
        }
    }
}