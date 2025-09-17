package net.thechance.mena.core_chat.presentation.screen.syncContacts

sealed interface SyncContactsScreenEffect {
    object NavigateBack : SyncContactsScreenEffect
    object NavigateToContacts : SyncContactsScreenEffect
    object NavigateBackWithResult : SyncContactsScreenEffect
}