package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val senderId: String,
    val chatId: String,
    val text: String? = null,
    val imageUrl: String? = null,
    val sendAt: String,
    val isRead: Boolean,
    val isMine: Boolean
)

@Serializable
data class SendMessageDto(
    val chatId: String,
    val text: String
)

@Serializable
data class MarkAsReadRequest(
    val chatId: String
)

@Serializable
data class MarkAsReadResponse(
    val readByUserId: String,
    val chatId: String,
    val readByMe: Boolean
)