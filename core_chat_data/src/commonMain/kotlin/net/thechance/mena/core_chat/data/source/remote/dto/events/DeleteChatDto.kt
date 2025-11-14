package net.thechance.mena.core_chat.data.source.remote.dto.events

import kotlinx.serialization.Serializable

@Serializable
data class DeleteChatDto(
    val chatId: String,
)