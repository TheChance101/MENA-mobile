package net.thechance.mena.wallet.repository.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.wallet.data.network_client.NetworkClient

fun createNetworkClient(
    getRespond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = defaultResponse,
    postRespond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = defaultResponse,
    putRespond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = defaultResponse,
    deleteRespond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = defaultResponse
): NetworkClient {
    val getClient = getClient(getRespond)
    val postClient = getClient(postRespond)
    val putClient = getClient(putRespond)
    val deleteClient = getClient(deleteRespond)

    return object : NetworkClient {
        override suspend fun get(
            urlString: String,
            requestBuilder: HttpRequestBuilder.() -> Unit
        ): HttpResponse {
            return getClient.get(urlString, requestBuilder)
        }

        override suspend fun post(
            urlString: String,
            requestBuilder: HttpRequestBuilder.() -> Unit
        ): HttpResponse {
            return postClient.post(urlString, requestBuilder)
        }

        override suspend fun put(
            urlString: String,
            requestBuilder: HttpRequestBuilder.() -> Unit
        ): HttpResponse {
            return putClient.put(urlString, requestBuilder)
        }

        override suspend fun delete(
            urlString: String,
            requestBuilder: HttpRequestBuilder.() -> Unit
        ): HttpResponse {
            return deleteClient.delete(urlString, requestBuilder)
        }

        override fun clearCachedToken() {
            // No-op for tests
        }
    }
}

private fun getClient(
    respond: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
): HttpClient {
    return HttpClient(MockEngine { request -> respond(request) }) {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}

private val defaultResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
    {
        error("no response specified")
    }