package net.thechance.mena.core_chat.domain.model

sealed class SyncState {
    object Offline : SyncState()
    object Success : SyncState()
    data class Error(val error: Throwable) : SyncState()
}