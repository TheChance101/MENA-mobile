package net.thechance.mena.identity.data.utils

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
import net.thechance.mena.identity.data.datasource.LocalDataSource

fun authInterceptor(
    localDataSource: LocalDataSource
) = createClientPlugin("AuthInterceptor") {

    onRequest { request, _ ->
        if (!request.url.toString().contains("login")) {
            localDataSource.getAccessToken().let { token ->
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}

fun provideHttpClient(
    engine: HttpClientEngine,
    localDataSource: LocalDataSource,
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
        install(authInterceptor(localDataSource))
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
}


