package net.thechance.mena.trends.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.trends.data.repository.ReelsRepositoryImpl

val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())


fun createReelsRepository(
    getReels: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): ReelsRepositoryImpl{
    return ReelsRepositoryImpl(createReelsHttpClient(getReels))
}


fun createReelsHttpClient(
    getReels: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    return HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/trends/reels" -> {
                getReels?.invoke(this) ?: getReelsResponse()
            }

            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
}