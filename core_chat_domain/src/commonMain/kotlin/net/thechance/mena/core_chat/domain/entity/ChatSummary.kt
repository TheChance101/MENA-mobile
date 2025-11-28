@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatSummary(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val lastMessage: Message?,
    val unReadMessagesCount: Int
) {
    data class Message(
        val type: LastMessageType,
        val sendAt: LocalDateTime,
        val isMine: Boolean
    )
}

sealed class LastMessageType {
    data class Text(val text: String) : LastMessageType()
    data object Image : LastMessageType()
    data object Audio : LastMessageType()
    data object Money : LastMessageType()
    data object Ayah : LastMessageType()
    data object Order : LastMessageType()
}