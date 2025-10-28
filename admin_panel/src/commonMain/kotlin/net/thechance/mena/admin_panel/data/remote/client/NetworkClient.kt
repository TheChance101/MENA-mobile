package net.thechance.mena.admin_panel.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Provided

class NetworkClient(
    @Provided
    @Named("baseUrl")
    private val baseUrl: String
){

    fun provideHttpClient(): HttpClient = buildClient()

    private fun buildClient(): HttpClient {
        return HttpClient(CIO) {
            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
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
                logger = Logger.Companion.SIMPLE
                level = LogLevel.ALL
            }

            install(plugin = Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = "", /*TODO() add access token*/
                            refreshToken = "" /*TODO() add refresh token*/
                        )
                    }
                    refreshTokens {
                        BearerTokens(
                            accessToken = "", /*TODO() add access token*/
                            refreshToken = "" /*TODO() add refresh token*/
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

    private companion object {
        const val TIME_OUT_INTERVAL_MILLI = 15_000L
    }
}