package net.thechance.mena.core_chat.data.source.remote.network

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
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService


interface HttpClientHolder {
    fun getClient(): HttpClient
    fun reset()
}

class HttpClientHolderImp(
    private val baseUrl: String,
    private val authorizationService: AuthorizationService,
    private val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>,
    private val json: Json
) : HttpClientHolder {
    private var client: HttpClient = createHttpClient()

    override fun getClient(): HttpClient {
        return client
    }

    override fun reset() {
        client.close()
        client = createHttpClient()
    }

    private fun createHttpClient(): HttpClient {
        val timeOutIntervalMilliSeconds = 30_000L
        return HttpClient(httpClientEngineFactory) {

            install(WebSockets)

            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }

            install(ContentNegotiation) { json(json) }

            install(plugin = Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = authorizationService.getAccessToken(),
                            refreshToken = authorizationService.getRefreshToken()
                        )
                    }
                    refreshTokens {
                        BearerTokens(
                            accessToken = authorizationService.getNewAccessToken(),
                            refreshToken = authorizationService.getRefreshToken()
                        )
                    }
                }
            }

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
}