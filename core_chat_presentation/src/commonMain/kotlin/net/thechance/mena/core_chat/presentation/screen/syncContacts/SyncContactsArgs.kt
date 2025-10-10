package net.thechance.mena.core_chat.presentation.screen.syncContacts

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute

interface SyncContactsScreenArgs {
    val forceSync: Boolean
}

class SyncContactsScreenArgsImpl(savedStateHandle: SavedStateHandle): SyncContactsScreenArgs {
    override val forceSync = savedStateHandle.toRoute<SyncContactsRoute>().forceSync
}

const val IS_SYNC_SUCCESS = "is_sync_success"
