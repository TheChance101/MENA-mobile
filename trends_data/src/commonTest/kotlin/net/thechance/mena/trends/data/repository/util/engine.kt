package net.thechance.mena.trends.data.repository.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.trends.data.client.NetworkClient

val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

internal fun createReelsHttpClient(
    respond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        respond(
            content = """{}""",
            status = HttpStatusCode.OK,
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            )
        )
    }
): NetworkClient {

    val mockEngine = MockEngine { request ->
        respond(request)
    }

    val client = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }

    return object : NetworkClient {
        override suspend fun get(
            urlString: String,
            block: HttpRequestBuilder.() -> Unit
        ): HttpResponse = client.get(urlString, block)

        override suspend fun post(
            urlString: String,
            block: HttpRequestBuilder.() -> Unit
        ): HttpResponse = client.post(urlString, block)

        override suspend fun put(
            urlString: String,
            block: HttpRequestBuilder.() -> Unit
        ): HttpResponse = client.put(urlString, block)

        override suspend fun delete(
            urlString: String,
            block: HttpRequestBuilder.() -> Unit
        ): HttpResponse = client.delete(urlString, block)
    }
}

internal fun createCategoryHttpClient(
    respond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        respond(
            content = """{}""",
            status = HttpStatusCode.OK,
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            )
        )
    }
): NetworkClient {

    val mockEngine = MockEngine { request ->
        respond(request)
    }

    val client = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    return object : NetworkClient {
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
    }
}
