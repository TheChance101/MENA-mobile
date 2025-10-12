package net.thechance.mena.core_chat.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
interface ChatRepository {
    suspend fun sendMessage(message: Message)
    suspend fun loadMessages(chatId: Uuid): List<Message>
    suspend fun deleteMessage(message: Message)
    fun subscribeToMessages(chatId: Uuid): Flow<Message>
    fun observeReadMessages(): Flow<String>
    suspend fun getChatByContactUserId(userId : Uuid): Chat
    suspend fun getChatById(chatId : Uuid): Chat
    suspend fun disconnect()
    suspend fun getLocalMessages(chatId: Uuid): List<Message>
    suspend fun downloadImage(url: String)
}
