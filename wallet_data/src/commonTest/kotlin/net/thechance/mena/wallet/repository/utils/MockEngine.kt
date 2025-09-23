package net.thechance.mena.wallet.repository.utils

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
import net.thechance.mena.wallet.data.dto.BalanceDto
import net.thechance.mena.wallet.data.exceptions.ErrorDto
import net.thechance.mena.wallet.data.extension.NetworkClientImpl
import net.thechance.mena.wallet.data.repository.balance.BalanceRepositoryImpl


val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

fun MockRequestHandleScope.defaultBalanceResponse(balance: Double = 150.75) = respond(
    content = jsonSerialization.encodeToString(
        BalanceDto.serializer(),
        BalanceDto(balance)
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.errorResponse(
    code: HttpStatusCode,
    message: String
) = respond(
    content = jsonSerialization.encodeToString(
        ErrorDto.serializer(),
        ErrorDto(message)
    ),
    status = code
)


fun createBalanceRepository(
    balanceResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): BalanceRepositoryImpl {
    val client = HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/wallet/balance" -> balanceResponse?.invoke(this) ?: defaultBalanceResponse()
            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }

    return BalanceRepositoryImpl(NetworkClientImpl(client))
}

