package net.thechance.mena.core_chat.presentation.screen.syncContacts

data class SyncContactsScreenState(
    val isLoading: Boolean = false,
    val showSyncView: Boolean = false,
    val isFirstSync: Boolean = false,
    val isPermissionDeniedPermanently: Boolean = false,
    val isOpenSettingsCalled: Boolean = false,
)