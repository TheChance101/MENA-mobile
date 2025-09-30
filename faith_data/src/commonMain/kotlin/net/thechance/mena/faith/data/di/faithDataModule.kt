package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.remote.client.NetworkClient
import net.thechance.mena.faith.data.repository.BookmarkRepositoryImpl
import net.thechance.mena.faith.data.repository.QuranRepositoryImpl
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val faithDataModule = module {
    includes(platformModule())
    single<AyahDao> { get<QuranDatabase>().getAyaDao() }
    single<QuranRepository> { QuranRepositoryImpl(get()) }
    singleOf(::BookmarkRepositoryImpl) bind BookmarkRepository::class
    single {
        NetworkClient(
            authorizationService = get(),
            baseUrl = get<String>(named("baseUrl"))
        ).provideHttpClient()
    }
}