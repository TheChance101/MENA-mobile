package net.thechance.mena.core_chat.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
data class Message(
    val id: Uuid,
    val senderId: Uuid,
    val chatId: Uuid,
    val text: String,
    val sendAt: LocalDateTime,
    val status: MessageStatus
)