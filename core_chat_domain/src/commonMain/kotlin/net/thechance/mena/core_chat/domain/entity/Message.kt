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
    data class Image(val data: ImageData) : MessageContent
    data class Audio(val data: AudioData) : MessageContent
}

sealed interface ImageData {
    data class ImageUrl(val url: String) : ImageData
    data class ImageByteArray(val byteArray: ByteArray) : ImageData
}

sealed interface AudioData {
    data class AudioUrl(val url: String) : AudioData
    data class AudioByteArray(val byteArray: ByteArray) : AudioData
}