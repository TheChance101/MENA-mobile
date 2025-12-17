package net.thechance.mena.core_chat.data.source.remote.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE

expect val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>

fun createMediaHttpClient(
    httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
): HttpClient {
    val timeOutIntervalMilliSeconds = 30_000L

    return HttpClient(httpClientEngineFactory) {

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeOutIntervalMilliSeconds
            connectTimeoutMillis = timeOutIntervalMilliSeconds
            socketTimeoutMillis = timeOutIntervalMilliSeconds
        }
    }
}

fun createChatCoilClient(): HttpClient {
    return HttpClient(httpClientEngineFactory) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000L
            connectTimeoutMillis = 30_000L
            socketTimeoutMillis = 30_000L
        }
    }
}