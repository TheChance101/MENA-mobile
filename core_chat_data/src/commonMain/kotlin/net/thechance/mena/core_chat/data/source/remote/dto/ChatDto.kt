package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val requesterId: String,
    val receiverId: String,
)