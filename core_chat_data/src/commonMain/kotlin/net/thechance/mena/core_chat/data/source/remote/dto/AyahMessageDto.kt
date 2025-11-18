package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AyahMessageDto(
    val messageId: String,
    val chatId: String,
    val ayahNumber: Int,
    val suraNumber: Int,
    val ayahText: String
)