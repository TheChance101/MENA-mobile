@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlin.uuid.ExperimentalUuidApi

data class ChatScreenState(
    val chat: ChatUiState = ChatUiState(),
    val inputMessage: String = "",
    val chatListItems: List<ChatListItem> = emptyList(),
    val uiMessages: List<MessageUiState> = emptyList(),

    val isResendMessageDialogVisible: Boolean = false,

    val failedMessageToReSend: MessageUiState? = null
)
