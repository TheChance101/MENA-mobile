package net.thechance.mena.admin_panel.data.remote.client

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
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
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.refreshToken

@OptIn(ExperimentalSettingsApi::class)
fun provideHttpClient(
    baseUrl: String,
    settings: FlowSettings,
    refreshToken: suspend () -> Unit
): HttpClient {
    return HttpClient(engineFactory = CIO) {
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
                        accessToken = settings.accessToken.first(),
                        refreshToken=settings.refreshToken.first()
                    )

                }
                refreshTokens {
                    refreshToken()
                    BearerTokens(
                        accessToken = settings.accessToken.first(),
                        refreshToken=settings.refreshToken.first()
                    )
                }
                sendWithoutRequest { request ->
                    val path = request.url.encodedPath.removePrefix("/")
                    path !in listOf(LOGIN_ENDPOINT, REFRESH_ENDPOINT,COUNTRIES_ENDPOINT)
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

private const val TIME_OUT_INTERVAL_MILLI = 15_000L
private const val REFRESH_ENDPOINT = "identity/admin/authentication/refresh"
private const val LOGIN_ENDPOINT = "identity/admin/authentication/login"
private const val COUNTRIES_ENDPOINT = "identity/authentication/countries"