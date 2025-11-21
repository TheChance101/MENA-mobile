@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Message(
    val id: Uuid,
    val senderId: Uuid,
    val chatId: Uuid,
    val sendAt: LocalDateTime,
    val status: MessageStatus,
    val content: MessageContent,
    val isMine: Boolean,
    val reactions: List<MessageReaction> = emptyList(),
)

data class MessageReaction(
    val emoji: String,
    val userId: Uuid,
    val messageId: Uuid
)

sealed interface MessageContent {
    data class Text(val text: String) : MessageContent
    data class Image(val data: ImageData) : MessageContent
    data class Audio(val data: AudioData, val audioDurationMs: Long? = null) : MessageContent
    data class Ayah(
        val surahId: Int,
        val surahName: String,
        val ayahContent: String,
        val ayahNumber: Int
    ) : MessageContent
}

sealed interface ImageData {
    data class ImageUrl(val url: String) : ImageData
    data class ImageByteArray(val byteArray: ByteArray) : ImageData
}

sealed interface AudioData {
    data class AudioUrl(val url: String) : AudioData
    data class AudioByteArray(val byteArray: ByteArray) : AudioData
}