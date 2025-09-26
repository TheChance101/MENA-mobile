package net.thechance.mena.core_chat.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ChatEffectorImpl
import net.thechance.mena.core_chat.presentation.screen.chats.ChatsViewModel
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsViewModel
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreenArgs
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreenArgsImpl
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsViewModel
import net.thechance.mena.core_chat.presentation.utils.SettingsOpener
import net.thechance.mena.core_chat.presentation.utils.SettingsOpenerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val viewModelModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    viewModelOf(::ChatsViewModel)
    viewModelOf(::ContactsViewModel)
    viewModelOf(::SyncContactsViewModel)
    factoryOf(::SyncContactsScreenArgsImpl) bind SyncContactsScreenArgs::class
    singleOf(::ChatEffectorImpl) bind ChatEffector::class
    factoryOf(::SettingsOpenerImpl) bind SettingsOpener::class
}