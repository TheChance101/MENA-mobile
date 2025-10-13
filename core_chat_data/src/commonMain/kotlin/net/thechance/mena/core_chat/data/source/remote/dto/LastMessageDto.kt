package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LastMessageDto(
    @SerialName("text") val content: String,
    @SerialName("sentAt") val sentAt: String,
    @SerialName("isMine") val isMine: Boolean,
)