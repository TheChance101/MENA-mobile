package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.chat.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.contacts.ContactsRepositoryImpl
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_CLIENT
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_JSON
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal val repositoryModule = module {
    single<ContactsRepository> {
        ContactsRepositoryImpl(
            client = get(named(CHAT_CLIENT)),
            authenticationRepository = get(),
            contactsProvider = get(),
            dataStore = get()
        )
    }

    single<ChatRepository> {
        ChatRepositoryImpl(
            client = get(named(CHAT_CLIENT)),
            json = get(named(CHAT_JSON)),
            authenticationRepository = get(),
            webSocketManager = get()
        )
    }
}
