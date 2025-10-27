package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteChatDto(
    val chatId: String,
    val success: Boolean,
    val message: String,
)
