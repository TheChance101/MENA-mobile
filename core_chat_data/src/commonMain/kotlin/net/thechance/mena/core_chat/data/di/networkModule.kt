package net.thechance.mena.core_chat.data.di

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import net.thechance.mena.core_chat.data.network.ApiConstants.BASE_URL
import net.thechance.mena.core_chat.data.network.createHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val networkModule = module {
    single(named("baseUrl")) { BASE_URL }
    single { createHttpClientEngine() }
    single { createHttpClient(get(named("baseUrl")), get()) }
}

expect fun createHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig>