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
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService
import org.koin.core.annotation.Named
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single


@Single(binds = [NetworkClient::class])
class NetworkClientImpl(
    @Provided private val authorizationService: AuthorizationService,
    @Provided @Named(BASE_URL) private val baseUrl: String
) : NetworkClient {

    private val client: HttpClient = provideHttpClient()

    override suspend fun get(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.get(urlString, block)
    }

    override suspend fun post(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.post(urlString, block)
    }

    override suspend fun put(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.put(urlString, block)
    }

    override suspend fun delete(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.delete(urlString, block)
    }

    fun provideHttpClient(): HttpClient {
        return HttpClient {
            defaultRequest {
                url(baseUrl)
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