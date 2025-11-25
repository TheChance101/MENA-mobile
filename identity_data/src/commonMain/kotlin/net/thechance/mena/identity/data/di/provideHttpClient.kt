package net.thechance.mena.identity.data.di

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
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl.Companion.LOGIN_ENDPOINT
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl.Companion.REFRESH_ENDPOINT
import net.thechance.mena.identity.data.repository.RegisterRepositoryImpl.Companion.REGISTER
import net.thechance.mena.identity.data.repository.RegisterRepositoryImpl.Companion.REGISTER_CHECK_USER_EXISTENCE
import net.thechance.mena.identity.data.repository.RegisterRepositoryImpl.Companion.REGISTER_REQUEST_OTP
import net.thechance.mena.identity.data.repository.RegisterRepositoryImpl.Companion.REGISTER_VERIFY_OTP
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl.Companion.RESET_PASSWORD
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl.Companion.RESET_PASSWORD_REQUEST_OTP
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl.Companion.RESET_PASSWORD_VERIFY_OTP
import net.thechance.mena.identity.domain.service.AuthorizationService

internal fun provideHttpClient(
    engine: HttpClientEngine,
    baseUrl: String,
    authorizationService: suspend () -> AuthorizationService,
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
                    BearerTokens(
                        accessToken = authorizationService().getAccessToken(),
                        refreshToken = authorizationService().getRefreshToken(),
                    )
                }
                refreshTokens {
                    BearerTokens(
                        accessToken = authorizationService().getNewAccessToken(),
                        refreshToken = authorizationService().getRefreshToken(),
                    )
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
    LOGIN_ENDPOINT,
    REFRESH_ENDPOINT,
    RESET_PASSWORD_REQUEST_OTP,
    RESET_PASSWORD_VERIFY_OTP,
    RESET_PASSWORD,
    REGISTER_REQUEST_OTP,
    REGISTER_VERIFY_OTP,
    REGISTER_CHECK_USER_EXISTENCE,
    REGISTER
)