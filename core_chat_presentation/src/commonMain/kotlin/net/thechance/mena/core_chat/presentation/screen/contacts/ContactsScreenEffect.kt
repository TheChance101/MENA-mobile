package net.thechance.mena.core_chat.presentation.screen.contacts

sealed class ContactsScreenEffect {
    object NavigateBack : ContactsScreenEffect()
    object NavigateToSyncContacts : ContactsScreenEffect()
    data class NavigateToChatScreen(val contactId: Int) : ContactsScreenEffect()
}