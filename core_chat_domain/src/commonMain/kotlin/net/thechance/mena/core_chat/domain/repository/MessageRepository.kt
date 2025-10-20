package net.thechance.mena.core_chat.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.domain.entity.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.entity.Message
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
interface
MessageRepository {
    suspend fun sendMessage(message: Message)
    suspend fun loadMessages(chatId: Uuid): List<Message>
    suspend fun deleteMessage(message: Message)
    fun observeMessagesForChatOrAll(chatId: Uuid? = null): Flow<Message>
    fun observeReadMessages(): Flow<MarkMessageAsReadEvent>
    fun observePendingMessagesByChatId(chatId: Uuid): Flow<List<Message>>
    suspend fun markMessagesOfChatAsRead(chatId: Uuid)
}