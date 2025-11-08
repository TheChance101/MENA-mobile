package net.thechance.mena.dukan.data.util.network

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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService


expect val platformHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>


fun buildApiClient(
    authorizationService: AuthorizationService,
    baseUrl: String
): HttpClient {
    return HttpClient(platformHttpClientEngineFactory) {

        defaultRequest {
            url(baseUrl)
        }

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

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("Dukan Client: $message")
                }
            }
        }

        install(Auth) {
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
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }
}