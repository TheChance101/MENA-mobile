package net.thechance.mena.core_chat.presentation.screen.home

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isSynced: Boolean = false,
    val balance: Double = 0.0,
    val chats: List<HomeUiState> = emptyList()
) {
    data class HomeUiState @OptIn(ExperimentalUuidApi::class) constructor(
        val id: Uuid,
        val name: String,
        val imageUrl: String?,
        val lastMessage: String,
        val time: String,
        val status: Status,
        val isMine: Boolean,
    ) {
        sealed class Status {
            data class UnRead(val count: Int) : Status()
            data object Read : Status()
            data object Sent : Status()
            data object Received : Status()
        }
    }
}