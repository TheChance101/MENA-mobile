package net.thechance.mena.core_chat.data.repository

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
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.toCached
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.toDomain
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfChatSummary
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.exception.NoInternetException
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.OperationFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val cachedChatSummaryDao: CachedChatSummaryDao,
) : ChatRepository {

    val _chatSummaries = MutableSharedFlow<List<ChatSummary>>()
    val _syncState = MutableSharedFlow<SyncState>()
    val scope = CoroutineScope(Dispatchers.IO)

    override fun observeChats(): Flow<List<ChatSummary>> {
        return _chatSummaries
    }

    override fun observeSyncState(): Flow<SyncState> {
        return _syncState
    }

    override suspend fun getChatsSummary(pageNumber: Int, pageSize: Int): PagedData<ChatSummary> {

        val cachedData = cachedChatSummaryDao.getChatSummaries(
                pageSize = pageSize,
                offset = pageNumber * pageSize
            ).sortedByDescending {
                it.lastMessageSentAt
            }.map { it.toDomain() }

        println("====> ****** showing cached data now ******")
        println("====> ****** cachedData: ${cachedData} ******")
        val totalItems = cachedChatSummaryDao.getChatSummariesCount()
        val isLastPage = cachedData.size < pageSize && cachedData.isNotEmpty()
        val result = PagedData(data = cachedData, totalItems = totalItems, isLastPage = isLastPage)

        scope.launch {
            syncChatSummaries(pageNumber, pageSize, cachedData)
        }
        return result
    }

     private suspend fun syncChatSummaries(startingPage: Int, pageSize: Int, localData: List<ChatSummary>) {

        if (startingPage == 0 || localData.isEmpty()) {
            println("====> ****** start syncing now ******")
            var currentPage = startingPage
            var shouldContinueFetching = true

            do {
                try {
                    val remoteChatSummaries = tryNetworkCall<PagedDataDto<ChatSummaryDto>>(
                        bodyType = typeInfo<PagedDataDto<ChatSummaryDto>>()
                    ) {
                        client.get(CHATS_SUMMARIES_ENDPOINT) {
                            parameter(PAGE_NUMBER_PARAMETER, currentPage)
                            parameter(PAGE_SIZE_PARAMETER, pageSize)
                        }
                    }?.toPagedListOfChatSummary()
                        ?: throw NotFoundException("Response body is null")

                    val remoteChatSummariesData = remoteChatSummaries.data
                    println("====> ****** remoteChatSummaries: ${remoteChatSummariesData.size} ******")

                    if (remoteChatSummariesData.isEmpty()) shouldContinueFetching = false

                    if (remoteChatSummariesData.isNotEmpty()) {
                        cachedChatSummaryDao.insertMultipleChatSummaries(remoteChatSummariesData.map {
                            it.toCached()
                        })

                        _chatSummaries.emit(remoteChatSummariesData)
                    }
                    currentPage++
                }
                catch (_: NoInternetException){
                    println("====> ****** IOException: you're offline ******")
                    _syncState.emit(SyncState.Offline)
                    return
                }
                catch (e: Throwable) {
                    //if (currentPage == 0) return
                    _syncState.emit(SyncState.Error(e))
                    return
                }

            } while (shouldContinueFetching && localData.isNotEmpty())
            _syncState.emit(SyncState.Success)
        }
    }

    override suspend fun getChatSummaryById(chatId: Uuid): ChatSummary {
        return tryNetworkCall<ChatSummaryDto>(
            bodyType = typeInfo<ChatSummaryDto>()
        ) {
            client.get(getChatSummaryEndpoint(chatId))
        }?.toDomain() ?: throw NotFoundException("Chat not found")
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

    override suspend fun deleteChatById(chatId: Uuid) {
        tryNetworkCall<Unit>(
            bodyType = typeInfo<Unit>(),
            defaultException = OperationFailedException("failed to delete message from data")
        ) {
            client.delete("$DELETE_CHAT_ENDPOINT/$chatId")
        }
        cachedChatSummaryDao.deleteChatSummaryById(chatId.toString())
    }

    override suspend fun getChatById(chatId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(bodyType = typeInfo<ChatDto>()) {
            client.get("$CHAT_ENDPOINT/$chatId")
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }


    override suspend fun disconnect() {
        webSocketManager.disconnect()
    }

    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val RECEIVER_ID_PARAMETER = "receiverId"
        const val CHAT_ENDPOINT = "/chat"
        const val DELETE_CHAT_ENDPOINT = "/chat/delete"
        const val CHATS_SUMMARIES_ENDPOINT = "/chat/chatsSummary"

        fun getChatSummaryEndpoint(chatId: Uuid): String {
            return "/chat/${chatId}/summary"
        }
    }
}