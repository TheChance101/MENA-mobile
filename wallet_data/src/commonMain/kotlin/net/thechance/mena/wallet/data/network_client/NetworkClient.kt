package net.thechance.mena.wallet.data.network_client

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse


interface NetworkClient {
    suspend fun get(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse

    suspend fun post(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse

    suspend fun put(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse

    suspend fun delete(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse
}