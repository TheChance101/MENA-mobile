package net.thechance.mena.core_chat.data.di

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.chat.utils.WebSocketManager
import net.thechance.mena.core_chat.data.chat.utils.WebSocketManagerImpl
import net.thechance.mena.core_chat.data.network.ApiConstants.BASE_URL
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_CLIENT
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_JSON
import net.thechance.mena.core_chat.data.network.createHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val networkModule = module {
    single(named(CHAT_JSON)) {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }

    single { createHttpClientEngine() }
    single(named(CHAT_CLIENT)) { createHttpClient(get(named(BASE_URL)), get()) }
    single<WebSocketManager> {
        WebSocketManagerImpl(
            baseUrl = get(named(BASE_URL)),
            client = get(named(CHAT_CLIENT))
        )
    }
}

expect fun createHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig>