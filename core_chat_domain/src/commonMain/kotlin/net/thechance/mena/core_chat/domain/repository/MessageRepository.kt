package net.thechance.mena.core_chat.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
interface
MessageRepository {
    suspend fun sendMessage(message: Message)
    suspend fun loadMessages(chatId: Uuid, page: Int, pageSize: Int): PagedData<Message>
    suspend fun deleteMessageById(messageId: Uuid)
    fun observeMessagesForChatOrAll(chatId: Uuid? = null): Flow<Message>
    fun observeReadMessages(): Flow<MarkMessageAsReadEvent>
    fun observeDeleteChat(): Flow<DeleteChatEvent>
    fun observePendingMessagesByChatId(chatId: Uuid): Flow<List<Message>>
    suspend fun markMessagesOfChatAsRead(chatId: Uuid)
    fun observeConnectionStatus(chatId: Uuid): Flow<Boolean>
    suspend fun addMessageReaction(messageId: Uuid, emoji: String)
    suspend fun removeMessageReaction(messageId: Uuid, emoji: String)
    fun observeMessageReactions(): Flow<MessageReaction>
    fun observeRemovedMessageReactions(): Flow<MessageReaction>
}