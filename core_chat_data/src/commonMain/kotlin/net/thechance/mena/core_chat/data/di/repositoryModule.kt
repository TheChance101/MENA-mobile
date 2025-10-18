package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.repository.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.repository.ContactsRepositoryImpl
import net.thechance.mena.core_chat.data.repository.UserRepositoryImpl
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal val repositoryModule = module {
    single<ContactsRepository> {
        ContactsRepositoryImpl(
            client = get(named(CHAT_CLIENT)),
            contactsProvider = get(),
            dataStore = get()
        )
    }

    single<ChatRepository> {
        ChatRepositoryImpl(
            client = get(named(CHAT_CLIENT)),
            json = get(named(CHAT_JSON)),
            webSocketManager = get(),
            messageDao = get(),
            imageDownloader = get()
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            client = get(named(CHAT_CLIENT)),
        )
    }
}
