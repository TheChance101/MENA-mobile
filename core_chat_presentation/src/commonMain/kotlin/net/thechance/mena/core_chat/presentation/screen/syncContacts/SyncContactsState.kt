package net.thechance.mena.core_chat.presentation.screen.syncContacts

import net.thechance.mena.core_chat.presentation.components.SnackBarData

data class SyncContactsState(
    val isLoading: Boolean = false,
    val showSyncView: Boolean = false,
    val isFirstSync: Boolean = false,
    val deniedPermanently: Boolean = false,
    val snackBarData: SnackBarData? = null,
    )