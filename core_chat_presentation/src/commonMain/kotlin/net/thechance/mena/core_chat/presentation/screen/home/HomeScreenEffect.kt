package net.thechance.mena.core_chat.presentation.screen.home

sealed interface HomeScreenEffect {
    object NavigateToWallet : HomeScreenEffect
    object NavigateToContacts : HomeScreenEffect
    object NavigateToSyncContacts : HomeScreenEffect
    data class NavigateToChat(val chatId: String, val chatName: String) : HomeScreenEffect
}