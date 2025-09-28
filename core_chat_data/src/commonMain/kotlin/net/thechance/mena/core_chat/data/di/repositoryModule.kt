package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.chat.FakeChatRepository
import net.thechance.mena.core_chat.data.contacts.ContactsRepositoryImpl
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal val repositoryModule = module {
    singleOf(::ContactsRepositoryImpl) bind ContactsRepository::class
    singleOf(::FakeChatRepository) bind ChatRepository::class
}
