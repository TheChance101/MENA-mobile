@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.model

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


sealed interface ChatListItem {
    data class DateSeparator(val label: String) : ChatListItem
    data class Message(val data: MarkedMessageUiState) : ChatListItem
}


data class TextMessageUiState(
    val id: Uuid = Uuid.random(),
    val senderId: Uuid = Uuid.random(),
    val chatId: Uuid = Uuid.random(),
    val sendTime: LocalDateTime,
    val status: MessageStatusUiState,
    val isMine: Boolean,
    val text: String
)

enum class MessageStatusUiState {
    SENDING,
    SENT,
    READ,
    FAILED
}

data class MarkedMessageUiState(
    val message: TextMessageUiState,
    val isMarkedLastInSeries: Boolean,
    val showMessageInfo: Boolean = false
)