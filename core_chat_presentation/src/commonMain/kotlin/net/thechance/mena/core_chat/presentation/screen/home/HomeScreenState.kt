package net.thechance.mena.core_chat.presentation.screen.home

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isSynced: Boolean = false,
    val isError: Boolean = false,
    val balanceAmount: Double = 0.0,
    val chats: List<ChatUiState> = emptyList()
) {
    data class ChatUiState @OptIn(ExperimentalUuidApi::class) constructor(
        val id: Uuid,
        val name: String,
        val imageUrl: String?,
        val lastMessage: MessageUiState,
        val status: Status,
    ) {
        data class MessageUiState(
            val text: String,
            val time: String,
            val isMine: Boolean,
        )

        sealed class Status {
            data class UnRead(val count: Int) : Status()
            data object Read : Status()
            data object Sent : Status()
            data object Received : Status()
        }
    }
}