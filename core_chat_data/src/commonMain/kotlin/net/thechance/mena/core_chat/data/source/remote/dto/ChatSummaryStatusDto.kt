package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSummaryStatusDto(
    @SerialName("isMine") val isMine: Boolean? = null,
    @SerialName("unReadMessagesCount") val unReadMessagesCount: Int? = null
)