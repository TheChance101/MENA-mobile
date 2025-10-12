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

sealed interface MessageContent {
    data class Text(val text: String) : MessageContent
    data class Images(val source: ImagesSource) : MessageContent
}

sealed interface ImagesSource {
    data class Remote(val urls: List<String>) : ImagesSource
    data class Local(val byteArrays: List<ByteArray>) : ImagesSource
}