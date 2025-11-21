package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AyahMessageDto(
    @SerialName("messageId")
    val messageId: String,
    @SerialName("chatId")
    val chatId: String,
    @SerialName("ayahNumber")
    val ayahNumber: Int,
    @SerialName("suraNumber")
    val surahNumber: Int,
    @SerialName("ayahText")
    val ayahContent: String
)