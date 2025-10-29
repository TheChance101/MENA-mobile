package net.thechance.mena.admin_panel.di

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import io.ktor.client.HttpClient
import net.thechance.mena.admin_panel.AppEnvironment
import net.thechance.mena.admin_panel.data.remote.client.NetworkClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val platformNetworkModule: Module

val networkModule = module {
    single(named(BASE_URL_KEY)) { AppEnvironment.baseUrl }
    single { NetworkClient( get(named(BASE_URL_KEY))) }

    single<HttpClient>(named(ADMIN_PANEL_KEY)) {
        get<NetworkClient>().provideHttpClient()
    }

    single<Ktorfit>(named(ADMIN_PANEL_KEY)) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named(ADMIN_PANEL_KEY)))
            .baseUrl(get<String>(named(BASE_URL_KEY)))
            .converterFactories(ResponseConverterFactory())
            .build()
    }

    includes(platformNetworkModule)
}

private const val BASE_URL_KEY = "baseUrl"
internal const val ADMIN_PANEL_KEY = "adminPanel"