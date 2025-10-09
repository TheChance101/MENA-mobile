package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSummaryDto(
    @SerialName("id") val id: String? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("lastMessage") val lastMessage: String? = null,
    @SerialName("lastMessageTime") val lastMessageTime: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("status") val chatSummaryStatusDto: ChatSummaryStatusDto? = null
)