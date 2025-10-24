package net.thechance.mena.core_chat.presentation.screen.syncContacts

import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData

sealed interface SyncContactsScreenEffect {
    object NavigateBack : SyncContactsScreenEffect
    object NavigateToContactsAfterSyncSuccess : SyncContactsScreenEffect
    data class ShowSnackBar(val snackBarData: SnackBarData) : SyncContactsScreenEffect
}