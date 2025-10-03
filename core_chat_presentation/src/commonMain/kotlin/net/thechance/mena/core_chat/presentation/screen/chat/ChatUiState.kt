@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import net.thechance.mena.core_chat.domain.entity.Chat
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatUiState(
    val id: Uuid = Uuid.random(),
    val name: String = "",
    val avatarUrl: String = "",
    val requesterId: Uuid = Uuid.random()
)

sealed interface ChatListItem {
    data class DateSeparator(val label: String): ChatListItem
    data class Message(val data: MarkedMessageUiState): ChatListItem
}

fun Chat.toUi() = ChatUiState(
    id = id,
    name = name,
    avatarUrl = imageUrl.orEmpty(),
    requesterId = requesterId
)