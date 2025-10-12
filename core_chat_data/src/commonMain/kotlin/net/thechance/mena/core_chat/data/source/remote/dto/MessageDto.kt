package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val senderId: String,
    val chatId: String,
    val text: String,
    val sendAt: String,
    val isRead: Boolean
)

@Serializable
data class SendMessageDto(
    val chatId: String,
    val text: String,
)

@Serializable
data class MarkAsReadRequest(
    val chatId: String
)

@Serializable
data class MarkAsReadResponse(
    val readBy: String
)