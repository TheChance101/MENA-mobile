@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.screen.chat.ChatViewModel
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsViewModel
import net.thechance.mena.core_chat.presentation.screen.home.HomeViewModel
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

internal val viewModelModule = module {
    viewModel {
        HomeViewModel(
            contactsRepository = get(),
            effector = get(),
            chatRepository = get(),
            balanceRepository = get()
        )
    }
    viewModel {
        ContactsViewModel(get(), get(), get(), dispatcher = get(named(CHAT_IO_DISPATCHER)))
    }
    viewModel {
        SyncContactsViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            dispatcher = get(named(CHAT_IO_DISPATCHER))
        )
    }
    viewModel { ChatViewModel(get(), get(), get(), dispatcher = get(named(CHAT_IO_DISPATCHER))) }
}