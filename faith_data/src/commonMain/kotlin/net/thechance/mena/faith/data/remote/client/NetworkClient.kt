package net.thechance.mena.faith.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NetworkClient {
    fun provideHttpClient(): HttpClient {
        return configureBaseSettings()
    }

    private fun configureBaseSettings(): HttpClient {
        return HttpClient {
            defaultRequest {
                url(BASE_URL)
                contentType(io.ktor.http.ContentType.Application.Json)
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
                        println("Http client: $message")
                    }
                }
            }

            install(plugin = Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            //TODO: Add token
                            accessToken = ACCESS_TOKEN,
                            refreshToken = ""
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

    private companion object {
        const val BASE_URL = ""
        const val ACCESS_TOKEN = ""
    }
}