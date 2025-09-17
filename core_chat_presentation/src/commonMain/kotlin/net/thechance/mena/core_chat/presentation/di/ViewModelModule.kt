package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.screen.chats.ChatsViewModel
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsViewModel
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModelOf(::ChatsViewModel)
    viewModelOf(::ContactsViewModel)
    viewModelOf(::SyncContactsViewModel)
}