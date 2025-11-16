package net.thechance.mena.faith.data.remote.client

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService

class NetworkClient(
    private val authorizationService: AuthorizationService,
    private val baseUrl: String,
    private val engine: HttpClientEngine,
) {
    fun provideHttpClient(): HttpClient = configureBaseSettings()

    private fun configureBaseSettings(): HttpClient {
        return HttpClient(engine = engine) {
            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                    })
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(message = "Http client: $message", tag = "HttpClient")
                    }
                }
            }

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

            install(HttpTimeout) {
                requestTimeoutMillis = 20_000
                connectTimeoutMillis = 20_000
                socketTimeoutMillis = 20_000
            }
        }
    }
}