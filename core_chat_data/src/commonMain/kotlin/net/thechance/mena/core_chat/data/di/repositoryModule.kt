package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.chat.FakeChatRepository
import net.thechance.mena.core_chat.data.contacts.ContactsRepositoryImpl
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal val repositoryModule = module {
    single<ContactsRepository> { ContactsRepositoryImpl(get(named("chatClient")), get(), get(), get()) }
    singleOf(::FakeChatRepository) bind ChatRepository::class
}
