@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatSummary(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val lastMessage: Message?,
    val unReadMessagesCount: Int
)