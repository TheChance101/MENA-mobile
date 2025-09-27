package net.thechance.mena.core_chat.presentation.screen.chat

data class ChatUiState(
    val id: String = "",
    val name: String = "",
    val avatarUrl: String = ""
)

sealed interface ChatListItem {
    data class DateSeparator(val label: String): ChatListItem
    data class Message(val data: MarkedMessageUiState): ChatListItem
}