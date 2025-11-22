package net.thechance.mena.dukan.data.repository.mockEngine.search

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.dukan.DukanSearchDto
import net.thechance.mena.dukan.data.dto.product.ProductSearchDto
import net.thechance.mena.dukan.data.repository.SearchRepositoryImpl
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
fun MockRequestHandleScope.defaultDukanSearchResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(DukanSearchDto.serializer()),
        PageResponseDto(
            number = 1,
            size = 10,
            totalPages = 1,
            totalElements = 1,
            first = true,
            last = true,
            content = listOf(
                DukanSearchDto(
                    id = Uuid.random(),
                    name = "Test Dukan",
                    imageUrl = "https://example.com/dukan",
                    isFavorite = false
                )

            )
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

@OptIn(ExperimentalUuidApi::class)
fun MockRequestHandleScope.defaultProductSearchResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(ProductSearchDto.serializer()),
        PageResponseDto(
            number = 1,
            size = 10,
            totalPages = 1,
            totalElements = 1,
            first = true,
            last = true,
            content = listOf(
                ProductSearchDto(
                    id = Uuid.random(),
                    name = "Test Product",
                    dukanName = "Test Dukan",
                    dukanId = Uuid.random(),
                    price = 100.0,
                    mainImageUrl = "https://example.com/product.png",
                    isFavorite = false,
                    isOutOfStock = false
                )
            )
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)


@OptIn(ExperimentalUuidApi::class)
fun searchHttpClient(
    dukanSearchResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productSearchResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {

    return HttpClient(
        MockEngine { request ->

            when (request.url.encodedPath) {
                "/dukan/search" ->
                    dukanSearchResponse?.invoke(this)
                        ?: defaultDukanSearchResponse()

                "/dukan/products/search" ->
                    productSearchResponse?.invoke(this)
                        ?: defaultProductSearchResponse()

                else ->
                    respond(
                        content = "",
                        status = HttpStatusCode.BadRequest,
                        headers = jsonHeaders
                    )
            }
        }
    ) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
}

fun searchRepository(
    dukanSearchResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productSearchResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): SearchRepositoryImpl {
    return SearchRepositoryImpl(
        client = searchHttpClient(
            dukanSearchResponse = dukanSearchResponse,
            productSearchResponse = productSearchResponse
        )
    )
}
