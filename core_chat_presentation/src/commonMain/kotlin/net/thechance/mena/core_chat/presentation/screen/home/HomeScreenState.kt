package net.thechance.mena.core_chat.presentation.screen.home

import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.HomeUiState
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.HomeUiState.Status
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

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toUi(): HomeUiState{
    // TODO : I Need To Handle All Possible Status "You will become the ugliest person you have ever seen in your life
    return HomeUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage,
        time = lastMessageTime,
        status = Status.UnRead(12),
        isMine = status.isMine
    )
}