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
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.identity.domain.service.LocalizationService

expect val platformHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>

class DukanApiClient(
    private val authorizationService: AuthorizationService,
    private val localizationService: LocalizationService,
    private val baseUrl: String,
    private val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>,
) : DukanApi {
    private var client: HttpClient = buildClient()

    private fun buildClient(): HttpClient {
        return createHttpClient()
    }

    override fun getClient(): HttpClient {
        return client
    }

    override fun reset() {
        client.close()
        client = buildClient()
    }

    private fun createHttpClient(): HttpClient {
        return HttpClient(httpClientEngineFactory) {
            defaultRequest {
                url(baseUrl)
                val language = localizationService.getCurrentLanguage().iso
                println("Accept-Language Test: $language")
                header("Accept-Language", language)
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
}

fun buildDukanCoilClient(): HttpClient {
    return HttpClient(platformHttpClientEngineFactory) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }
}