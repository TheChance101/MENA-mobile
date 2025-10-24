package net.thechance.mena.core_chat.presentation.screen.contacts

import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData

sealed interface ContactsScreenEffect {
    object NavigateBack : ContactsScreenEffect
    object NavigateToSyncContacts : ContactsScreenEffect
    data class NavigateToChat(val chatId: String, val chatName: String) : ContactsScreenEffect
    data class ShowSnackBar(val snackBarData: SnackBarData) : ContactsScreenEffect
}