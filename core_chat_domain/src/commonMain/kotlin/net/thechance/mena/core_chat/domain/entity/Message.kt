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
    val content: MessageContent
)

sealed class MessageContent {
    data class Text(val text: String) : MessageContent()
    data class ImageUrls(val urls: List<String>) : MessageContent()
    data class PendingImages(val byteArrays: List<ByteArray>) : MessageContent()
}
