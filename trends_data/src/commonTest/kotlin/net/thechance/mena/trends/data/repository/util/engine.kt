package net.thechance.mena.trends.data.repository.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
): HttpClient {

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

    return client

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
): HttpClient {

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

    return client
}

internal fun mockUserInfoHttpClient(
    respond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        respond(
            content =
                """{
                    "username": "nour",
                    "firstName": "nour",
                    "lastName": "nour",
                    "profileImageUrl": "img.png"
                }""",
            status = HttpStatusCode.OK,
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            )
        )
    }
): HttpClient {

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

    return client
}