package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionDto(
    val messageId: String,
    val emoji: String,
    val userId: String
)

@Serializable
data class MessageReactionRequestDto(
    val messageId: String,
    val emoji: String
)
