package net.thechance.mena.core_chat.data.network

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

expect val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>

fun createHttpClient(
    baseUrl: String,
    authorizationService: AuthorizationService,
    httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
): HttpClient {
    val timeOutIntervalMilliSeconds = 30_000L
    return HttpClient(httpClientEngineFactory) {

        install(WebSockets)

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

        install(plugin = Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiNmMyYzMyZi0wOWIzLTRjM2EtYjFhYy00N2IzZDhjMjA0NGUiLCJpYXQiOjE3NTk3NDA0ODksImV4cCI6MTc1OTc0NDA4OX0.NjjWnoYwHXFQxMzD64VShS6g3EP48mKB6x9UCDLrWSk",
                        refreshToken = ""
                    )
                }
                refreshTokens {
                    BearerTokens(
                        accessToken = authorizationService.refreshToken(),
                        refreshToken = ""
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
