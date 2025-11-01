package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ChatDto(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val requesterId: String,
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class DeleteChatResponse(
    val deletedChatId: Uuid,
)