package net.thechance.mena.core_chat.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(
    baseUrl: String,
    httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
): HttpClient {
    val timeOutIntervalMilliSeconds = 30_000L
    return HttpClient(httpClientEngineFactory) {
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }

        install(Logging) { level = LogLevel.ALL }

        install(HttpTimeout) {
            requestTimeoutMillis = timeOutIntervalMilliSeconds
            connectTimeoutMillis = timeOutIntervalMilliSeconds
            socketTimeoutMillis = timeOutIntervalMilliSeconds
        }
    }
}
