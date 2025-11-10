package net.thechance.mena.trends.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
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
import net.thechance.mena.trends.data.util.getHttpEngine
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import co.touchlab.kermit.Logger as kermitLogger

@Single
class NetworkClient : KoinComponent {

    private val authorizationService: AuthorizationService by lazy { get() }
    private val baseUrl: String by lazy { get(named(BASE_URL_KEY)) }

    @Single
    fun provideDefaultHttpClient(): HttpClient {
        return HttpClient(engine = getHttpEngine()) {
            baseConfig(this)

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        kermitLogger.i(DEFAULT_HTTP_CLIENT_LOG_TAG) { message }
                    }
                }
            }

            install(HttpTimeout) {
                connectTimeoutMillis = TIME_OUT_INTERVAL_MILLI
                requestTimeoutMillis = TIME_OUT_INTERVAL_MILLI
            }
        }
    }

    @Single
    fun provideUploadHttpClient(): HttpClient {
        return HttpClient(engine = getHttpEngine()) {
            baseConfig(this)

            install(Logging) {
                level = LogLevel.HEADERS
                logger = object : Logger {
                    override fun log(message: String) {
                        kermitLogger.i(UPLOAD_HTTP_CLIENT_LOG_TAG) { message }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            }
        }
    }

    private fun baseConfig(builder: HttpClientConfig<*>) {
        builder.defaultRequest { url(baseUrl) }

        builder.install(plugin = Auth) {
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

        builder.install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private companion object {
        const val TIME_OUT_INTERVAL_MILLI = 60_000L
        const val BASE_URL_KEY = "baseUrl"
        const val UPLOAD_HTTP_CLIENT_LOG_TAG = "Upload Http Client"
        const val DEFAULT_HTTP_CLIENT_LOG_TAG = "Default Http Client"
    }
}