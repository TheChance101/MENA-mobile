package net.thechance.mena.core_chat.domain.repository

import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
interface ChatRepository {
    suspend fun getChatByContactUserId(userId: Uuid): Chat
    suspend fun getChatById(chatId: Uuid): Chat
    suspend fun disconnect()
    suspend fun getChatsSummary(pageNumber: Int, pageSize: Int): PagedData<ChatSummary>
    suspend fun getChatSummaryById(chatId: Uuid): ChatSummary
}
