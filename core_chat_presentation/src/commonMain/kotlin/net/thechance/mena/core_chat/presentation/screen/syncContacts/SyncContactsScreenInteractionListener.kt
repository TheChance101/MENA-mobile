package net.thechance.mena.core_chat.presentation.screen.syncContacts

interface SyncContactsScreenInteractionListener {
    fun onBackClick()
    fun onSyncClick()
    fun onForceSync(forceSync: Boolean)
    fun onSnackBarDismiss()
}