package net.thechance.mena.core_chat.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
data class Message(
    val id: Uuid,
    val senderId: Uuid,
    val chatId: Uuid,
    val sendAt: LocalDateTime,
    val status: MessageStatus,
    val content: MessageContent,
    val isMine: Boolean,
)

sealed interface MessageContent {
    data class Text(val text: String) : MessageContent
    data class Images(val source: ImageData) : MessageContent
}

sealed interface ImageData {
    data class ImageUrl(val urls: List<String>) : ImageData
    data class ImageByteArray(val byteArrays: List<ByteArray>) : ImageData
}