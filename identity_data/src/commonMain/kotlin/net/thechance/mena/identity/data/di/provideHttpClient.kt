package net.thechance.mena.identity.data.di

import com.russhwolf.settings.Settings
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
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.data.dataSource.local.setting.accessToken
import net.thechance.mena.identity.data.dataSource.local.setting.refreshToken
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl.Companion.LOGIN_ENDPOINT
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl.Companion.REFRESH_ENDPOINT
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl.Companion.REQUEST_OTP
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl.Companion.RESET_PASSWORD
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl.Companion.VERIFY_OTP

internal fun provideCoilClient(
    engine: HttpClientEngine,
    settings: Settings,
    baseUrl: String,
    refreshToken: suspend () -> Unit
): HttpClient {
    return HttpClient(engine) {
        defaultRequest { url(baseUrl) }

        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("Identity Client: $message")
                }
            }
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(settings.accessToken, settings.refreshToken)
                }
                refreshTokens {
                    refreshToken()
                    BearerTokens(settings.accessToken, settings.refreshToken)
                }
                sendWithoutRequest { request ->
                    val path = request.url.encodedPath.removePrefix("/")
                    path !in whiteListEndPoints
                }
            }
        }
        install(HttpTimeout) {
            connectTimeoutMillis = NETWORK_TIMEOUT_MS
            requestTimeoutMillis = NETWORK_TIMEOUT_MS
        }
    }
}

internal fun provideCoilClient(engine: HttpClientEngine): HttpClient {
    return HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = NETWORK_TIMEOUT_MS
            requestTimeoutMillis = NETWORK_TIMEOUT_MS
        }
    }
}

const val NETWORK_TIMEOUT_MS = 15_000L
private val whiteListEndPoints = listOf(
    LOGIN_ENDPOINT, REFRESH_ENDPOINT, REQUEST_OTP, VERIFY_OTP, RESET_PASSWORD
)