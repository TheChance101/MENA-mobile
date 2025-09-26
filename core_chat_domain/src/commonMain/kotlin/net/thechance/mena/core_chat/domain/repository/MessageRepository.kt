package net.thechance.mena.core_chat.domain.repository

import net.thechance.mena.core_chat.domain.entity.Message
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
interface MessageRepository {
    fun sendMessage(message: Message)
    fun loadMessages(chatId: Uuid): List<Message>
    fun markMessagesAsRead(chatId: Uuid)
    fun subscribeToMessages(chatId: Uuid):List<Message>
}