@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.api.ChatEntryViewModel
import net.thechance.mena.core_chat.presentation.screen.chat.ChatViewModel
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsViewModel
import net.thechance.mena.core_chat.presentation.screen.home.HomeViewModel
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.ShareMessageViewModel
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

internal val viewModelModule = module {
    viewModel {
        HomeViewModel(
            contactsRepository = get(),
            chatRepository = get(),
            balanceRepository = get(),
            messageRepository = get(),
            prayerTimeService = get(),
            locationService = get(),
            weatherRepository = get(),
            dispatcher = get(named(CHAT_IO_DISPATCHER))
        )
    }
    viewModel {
        ContactsViewModel(get(), get(), dispatcher = get(named(CHAT_IO_DISPATCHER)))
    }
    viewModel {
        SyncContactsViewModel(
            get(),
            get(),
            get(),
            get(),
            dispatcher = get(named(CHAT_IO_DISPATCHER))
        )
    }
    viewModel {
        ChatViewModel(
            chatRepository = get(),
            userRepository =  get(),
            audioRecordRepository = get(),
            chatArgs =  get(),
            permissionsController = get(),
            messageRepository = get(),
            imageDownloaderService = get(),
            audioPlayer = get(),
            dispatcher = get(named(CHAT_IO_DISPATCHER)),
        )
    }
    viewModel {
        ChatEntryViewModel(get())
    }
    viewModel {
        ShareMessageViewModel(get(), get(), get(), get())
    }
}
