package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable
import net.thechance.mena.core_chat.data.source.remote.dto.message.MessageContentDto
import net.thechance.mena.core_chat.data.source.remote.dto.message.MessageContentDtoSerializer

@Serializable
data class MessageDto(
    val id: String,
    val senderId: String,
    val chatId: String,
    val type: String,
    @Serializable(with = MessageContentDtoSerializer::class)
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