@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.core_chat.presentation.screen.chats.ChatsViewModel
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsViewModel
import net.thechance.mena.core_chat.presentation.screen.chat.ChatViewModel
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

internal val viewModelModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    viewModelOf(::ChatsViewModel)
    viewModelOf(::ContactsViewModel)
    viewModelOf(::SyncContactsViewModel)
    viewModelOf(::ChatViewModel)
}