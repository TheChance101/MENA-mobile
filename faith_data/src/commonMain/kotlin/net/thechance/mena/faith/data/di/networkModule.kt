package net.thechance.mena.faith.data.di

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import io.ktor.client.HttpClient
import net.thechance.mena.faith.data.remote.client.NetworkClient
import net.thechance.mena.faith.data.remote.service.BookmarkApiService
import net.thechance.mena.faith.data.remote.service.MosqueApiService
import net.thechance.mena.faith.data.remote.service.PrayerTimeApiService
import net.thechance.mena.faith.data.remote.service.TilawahApiService
import net.thechance.mena.faith.data.remote.service.createBookmarkApiService
import net.thechance.mena.faith.data.remote.service.createMosqueApiService
import net.thechance.mena.faith.data.remote.service.createPrayerTimeApiService
import net.thechance.mena.faith.data.remote.service.createTilawahApiService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient>(named(FAITH_HTTP_CLIENT_KEY)) {
        NetworkClient(
            authorizationService = get(),
            baseUrl = get(named(BASE_URL_KEY)),
            engine = get(),
        ).provideHttpClient()
    }

    single<Ktorfit>(named(FAITH_KTORFIT_KEY)) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named(FAITH_HTTP_CLIENT_KEY)))
            .baseUrl(get<String>(named(BASE_URL_KEY)) + "/")
            .converterFactories(ResponseConverterFactory())
            .build()
    }

    single<BookmarkApiService> {
        get<Ktorfit>(named(FAITH_KTORFIT_KEY)).createBookmarkApiService()
    }

    single<PrayerTimeApiService> {
        get<Ktorfit>(named(FAITH_KTORFIT_KEY)).createPrayerTimeApiService()
    }

    single<TilawahApiService> {
        get<Ktorfit>(named(FAITH_KTORFIT_KEY)).createTilawahApiService()
    }

    single<MosqueApiService> {
        get<Ktorfit>(named(FAITH_KTORFIT_KEY)).createMosqueApiService()
    }
}

private const val BASE_URL_KEY = "baseUrl"
private const val FAITH_HTTP_CLIENT_KEY = "faithHttpClient"
private const val FAITH_KTORFIT_KEY = "faithKtorfit"
