package net.thechance.mena.trends.data.client

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
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.trends.data.util.getHttpEngine
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

@Single
class NetworkClient : KoinComponent {

    private val authorizationService: AuthorizationService by lazy { get() }
    private val baseUrl: String by lazy { get(named(BASE_URL)) }

    @Single
    fun provideHttpClient(): HttpClient {
        return HttpClient(engine = getHttpEngine()) {
            defaultRequest {
                url(baseUrl)
            }

            install(Logging) {
                level = LogLevel.HEADERS
                filter { request ->
                    request.body !is MultiPartFormDataContent
                }
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
                            accessToken = authorizationService.getAccessToken(),
                            refreshToken = authorizationService.refreshToken()
                        )
                    }

                    refreshTokens {
                        val newAccessToken = authorizationService.refreshToken()
                        BearerTokens(
                            accessToken = newAccessToken,
                            refreshToken = authorizationService.refreshToken()
                        )
                    }
                }
            }

            install(HttpTimeout) {
                connectTimeoutMillis = TIME_OUT_INTERVAL_MILLI
                requestTimeoutMillis = TIME_OUT_INTERVAL_MILLI
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    private companion object {
        const val TIME_OUT_INTERVAL_MILLI = 15_000L
        const val BASE_URL = "baseUrl"
    }
}