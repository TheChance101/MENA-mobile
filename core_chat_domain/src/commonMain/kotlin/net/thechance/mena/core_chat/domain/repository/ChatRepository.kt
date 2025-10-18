package net.thechance.mena.core_chat.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
interface ChatRepository {
    suspend fun sendMessage(message: Message)
    suspend fun loadMessages(chatId: Uuid): List<Message>
    suspend fun deleteMessage(message: Message)
    fun getMessages(chatId: Uuid? = null): Flow<Message>
    fun observeReadMessages(): Flow<MarkMessageAsReadEvent>
    suspend fun getChatByContactUserId(userId: Uuid): Chat
    suspend fun getChatById(chatId : Uuid): Chat
    suspend fun disconnect()
    fun getLocalMessages(chatId: Uuid): Flow<List<Message>>
    suspend fun getChatsSummary(pageNumber: Int, pageSize: Int): PagedData<ChatSummary>
    suspend fun getChatSummaryById(chatId: Uuid): ChatSummary
    suspend fun downloadImage(url: String)
    suspend fun markMessagesAsRead(chatId: Uuid)
}
