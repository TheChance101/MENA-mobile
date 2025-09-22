package net.thechance.mena.wallet.data.extension

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

expect val platformHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>

@Single
class NetworkClientImpl(
    private val client: HttpClient = buildClient()
): NetworkClient {

    override suspend fun get(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.get(urlString, block)
    }

    override suspend fun post(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.post(urlString, block)
    }

    override suspend fun put(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.put(urlString, block)
    }

    override suspend fun delete(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.delete(urlString, block)
    }

    companion object {
        private fun buildClient(): HttpClient {
            return HttpClient(platformHttpClientEngineFactory) {
                defaultRequest { url(BASE_URL) }

                install(Logging) { level = LogLevel.ALL }

                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                        }
                    )
                }

                install(plugin = Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(
                                //TODO: Add token
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }
                }

                install(HttpTimeout) {
                    requestTimeoutMillis = TIME_OUT_INTERVAL_MILLI
                    connectTimeoutMillis = TIME_OUT_INTERVAL_MILLI
                    socketTimeoutMillis = TIME_OUT_INTERVAL_MILLI
                }
            }
        }
        private const val BASE_URL = "https://mena-dev.the-chance.net/"
        private const val TIME_OUT_INTERVAL_MILLI = 15_000L
    }

}