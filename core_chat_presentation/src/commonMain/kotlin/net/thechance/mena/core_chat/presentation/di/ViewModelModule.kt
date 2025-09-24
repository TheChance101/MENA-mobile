package net.thechance.mena.core_chat.presentation.di

import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ChatEffectorImpl
import net.thechance.mena.core_chat.presentation.screen.chats.ChatsViewModel
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsScreenArgs
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsViewModel
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreenArgs
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreenArgsImpl
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModelOf(::ChatsViewModel)
    viewModel { (isSyncSuccess: MutableStateFlow<Boolean>) ->
        ContactsViewModel(get(), ContactsScreenArgs(isSyncSuccess), get())
    }
    viewModelOf(::SyncContactsViewModel)
    factoryOf(::SyncContactsScreenArgsImpl) bind SyncContactsScreenArgs::class
    singleOf(::ChatEffectorImpl) bind ChatEffector::class
}