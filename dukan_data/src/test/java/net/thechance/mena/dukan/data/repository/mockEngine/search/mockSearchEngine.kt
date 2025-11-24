package net.thechance.mena.dukan.data.repository.mockEngine.search

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.serialization.kotlinx.json.json
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.dukan.DukanSearchDto
import net.thechance.mena.dukan.data.dto.product.ProductSearchDto
import net.thechance.mena.dukan.data.repository.SearchRepositoryImpl
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization

fun MockRequestHandleScope.defaultPaginatedSearchResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(DukanSearchDto.serializer()),
        PageResponseDto(
            content = demoSearchDukansDto,
            number = 0,
            size = 2,
            totalPages = 1,
            totalElements = 2,
            first = true,
            last = true
        )
    ),
    status = io.ktor.http.HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultPaginatedProductsResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(ProductSearchDto.serializer()),
        PageResponseDto(
            content = demoProductsDto,
            number = 0,
            size = 2,
            totalPages = 1,
            totalElements = 2,
            first = true,
            last = true
        )
    ),
    status = io.ktor.http.HttpStatusCode.OK,
    headers = jsonHeaders
)


fun createSearchHttpClient(
    searchDukansResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    searchProductsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): HttpClient {
    return HttpClient(MockEngine) {
        install(ContentNegotiation) {
            json()
        }
        engine {
            addHandler { request ->
                when (request.url.encodedPath) {
                    "/dukan/search" ->
                        searchDukansResponse?.invoke(this)
                            ?: defaultPaginatedSearchResponse()

                    "/dukan/search/categories/1" ->
                        searchDukansResponse?.invoke(this)
                            ?: defaultPaginatedSearchResponse()

                    "/dukan/products/search" ->
                        searchProductsResponse?.invoke(this)
                            ?: defaultPaginatedProductsResponse()

                    else ->
                        respond("", status = io.ktor.http.HttpStatusCode.BadRequest)
                }
            }
        }
    }
}

fun createSearchRepository(
    searchDukansResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    searchProductsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): SearchRepositoryImpl {
    return SearchRepositoryImpl(
        client = createSearchHttpClient(
            searchDukansResponse = searchDukansResponse,
            searchProductsResponse = searchProductsResponse
        )
    )
}