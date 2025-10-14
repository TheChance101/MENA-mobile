package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSummaryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("lastMessage") val lastMessage: LastMessageDto,
    @SerialName("unReadMessagesCount") val unReadMessagesCount: Int
) {
    @Serializable
    data class LastMessageDto(
        @SerialName("text") val content: String,
        @SerialName("sentAt") val sentAt: String,
        @SerialName("isMine") val isMine: Boolean,
    )
}