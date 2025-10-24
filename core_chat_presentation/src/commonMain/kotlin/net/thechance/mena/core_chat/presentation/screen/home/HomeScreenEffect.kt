package net.thechance.mena.core_chat.presentation.screen.home

import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData

sealed interface HomeScreenEffect {
    object NavigateToWallet : HomeScreenEffect
    object NavigateToContacts : HomeScreenEffect
    object NavigateToSyncContacts : HomeScreenEffect
    data class NavigateToChat(val chatId: String, val chatName: String) : HomeScreenEffect
    data class ShowSnackBar(val snackBarData: SnackBarData) : HomeScreenEffect
}