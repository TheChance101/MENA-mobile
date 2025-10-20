package net.thechance.mena.core_chat.presentation.screen.contacts

sealed interface ContactsScreenEffect {
    object NavigateBack : ContactsScreenEffect
    object NavigateToSyncContacts : ContactsScreenEffect
    data class NavigateToChat(val chatId: String, val chatName: String) : ContactsScreenEffect
}