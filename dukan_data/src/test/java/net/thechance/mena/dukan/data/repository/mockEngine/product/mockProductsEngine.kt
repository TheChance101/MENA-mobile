package net.thechance.mena.dukan.data.repository.mockEngine.product

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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.product.CreateProductResponse
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.data.repository.DukanProductRepositoryImpl
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization


fun MockRequestHandleScope.defaultCreateProductResponse() = respond(
    content = jsonSerialization.encodeToString(
        CreateProductResponse(productId = createdProductResponseId)
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)


fun MockRequestHandleScope.defaultPaginatedProductResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(ProductDto.serializer()),
        PageResponseDto(
            content = productDtos,
            number = 0,
            size = 2,
            totalPages = 1,
            totalElements = 2,
            first = true,
            last = true
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultImagesUploadResponse() = respond(
    content = jsonSerialization.encodeToString(
        ListSerializer(String.serializer()),
        dummyImageUrls
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultProductDetailsResponse() = respond(
    content = jsonSerialization.encodeToString(
        ProductDto.serializer(),
        productDto1
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)


fun createProductHttpClient(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    paginatedResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadImagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productDetailsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): HttpClient {
    return HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/dukan/product/create" -> createResponse?.invoke(this)
                ?: defaultCreateProductResponse()

            "/dukan/product" -> paginatedResponse?.invoke(this)
                ?: defaultPaginatedProductResponse()

            "/dukan/product/images/$createdProductResponseId" -> uploadImagesResponse?.invoke(this)
                ?: defaultImagesUploadResponse()

            "/dukan/product/$createdProductResponseId" -> productDetailsResponse?.invoke(this)
                ?: defaultProductDetailsResponse()

            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }

}


fun createProductRepository(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    paginatedResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadImagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productDetailsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): DukanProductRepositoryImpl {
    return DukanProductRepositoryImpl(
        client = createProductHttpClient(
            createResponse = createResponse,
            paginatedResponse = paginatedResponse,
            uploadImagesResponse = uploadImagesResponse,
            productDetailsResponse = productDetailsResponse
        )
    )
}