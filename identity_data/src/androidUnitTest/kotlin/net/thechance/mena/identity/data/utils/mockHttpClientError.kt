package net.thechance.mena.identity.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json

fun mockHttpClientError(status: HttpStatusCode): HttpClient {
        return io.ktor.client.HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json()
            }
            engine {
                addHandler {
                    respondError(
                        status = status
                    )
                }
            }
        }
    }