package net.thechance.mena.faith.data.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.remote.client.NetworkClient
import net.thechance.mena.faith.data.remote.service.BookmarkApiService
import net.thechance.mena.faith.data.remote.service.createBookmarkApiService
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
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class

    single<HttpClient>(named("faithHttpClient")) {
        NetworkClient(
            authorizationService = get(),
            baseUrl = get(named("baseUrl"))
        ).provideHttpClient()
    }

    single<Ktorfit>(named("faithKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("faithHttpClient")))
            .baseUrl(get<String>(named("baseUrl")))
            .build()
    }

    single<BookmarkApiService> {
        get<Ktorfit>(named("faithKtorfit")).createBookmarkApiService()
    }

    singleOf(::BookmarkRepositoryImpl) bind BookmarkRepository::class
}