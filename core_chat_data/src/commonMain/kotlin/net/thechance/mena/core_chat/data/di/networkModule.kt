package net.thechance.mena.core_chat.data.di

import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManagerImpl
import net.thechance.mena.core_chat.data.source.remote.network.createHttpClient
import net.thechance.mena.core_chat.data.source.remote.network.httpClientEngineFactory
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
    single(named(CHAT_CLIENT)) {
        createHttpClient(
            get(named(BASE_URL)),
            get(),
            httpClientEngineFactory,
            get(named(CHAT_JSON))
        )
    }
    single<WebSocketManager> {
        WebSocketManagerImpl(
            baseUrl = get(named(BASE_URL)),
            client = get(named(CHAT_CLIENT))
        )
    }
}

private const val BASE_URL = "baseUrl"
const val CHAT_CLIENT = "chatClient"
const val CHAT_JSON = "chatJson"
