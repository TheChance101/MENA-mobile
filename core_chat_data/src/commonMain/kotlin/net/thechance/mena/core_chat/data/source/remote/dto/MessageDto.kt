package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val senderId: String,
    val chatId: String,
    val content: MessageContentDto,
    val reactions: List<MessageReactionDto> = emptyList(),
    val sendAt: String,
    val isRead: Boolean,
    val isMine: Boolean
)

@Serializable
data class SendMessageDto(
    val messageId: String,
    val chatId: String,
    val text: String? = null
)

@Serializable
data class MarkAsReadRequest(
    val chatId: String
)

@Serializable
data class MarkAsReadDto(
    val readByUserId: String,
    val chatId: String,
    val readByMe: Boolean
)