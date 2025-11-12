package net.thechance.mena.core_chat.domain.model

import net.thechance.mena.core_chat.domain.entity.ChatSummary
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class SyncState {
    data object Offline : SyncState()
    data class ChatsSummariesSynced(val chatSummaries: List<ChatSummary>) : SyncState()

    @OptIn(ExperimentalUuidApi::class)
    data class DeletedChatsSynced(val chatIds: List<Uuid>) : SyncState()

    data class Error(val error: Throwable) : SyncState()
}