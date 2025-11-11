package net.thechance.mena.core_chat.domain.model

import kotlin.uuid.ExperimentalUuidApi

sealed class SyncState {
    object Offline : SyncState()
    object ChatsSummariesSyncedSuccess : SyncState()

    @OptIn(ExperimentalUuidApi::class)
    object DeletedChatsSyncedSuccess : SyncState()

    data class Error(val error: Throwable) : SyncState()
}