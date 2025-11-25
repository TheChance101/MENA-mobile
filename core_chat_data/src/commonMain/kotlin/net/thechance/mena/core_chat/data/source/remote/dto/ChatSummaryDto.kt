package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSummaryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("lastMessage") val lastMessage: MessageDto?,
    @SerialName("unReadMessagesCount") val unReadMessagesCount: Int
)