package net.thechance.mena.identity.data.di

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.data.utils.accessToken

internal fun provideHttpClient(
    engine: HttpClientEngine,
    settings: Settings,
    baseUrl: String,
): HttpClient {

    return HttpClient(engine) {
        defaultRequest {
            url(baseUrl)
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                })
        }
        install(authInterceptor(settings))
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
}

private fun authInterceptor(
    settings: Settings
) = createClientPlugin("AuthInterceptor") {

    onRequest { request, _ ->
        //TODO refactore hardcoded string to whitelist endpoints doesn't require token like register and reset password
        if (!request.url.toString().contains("login")) {
            settings.accessToken.let { token ->
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}


