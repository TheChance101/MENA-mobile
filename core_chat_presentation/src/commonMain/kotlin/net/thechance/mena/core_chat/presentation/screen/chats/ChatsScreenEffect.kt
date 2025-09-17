package net.thechance.mena.core_chat.presentation.screen.chats

sealed interface ChatsScreenEffect {
    object NavigateToContacts : ChatsScreenEffect
    object NavigateToSyncContacts : ChatsScreenEffect
}