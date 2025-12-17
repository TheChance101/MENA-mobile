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
import net.thechance.mena.dukan.data.repository.mockEngine.MockDukanApiClient
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization
import net.thechance.mena.dukan.data.util.network.DukanApi


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

fun MockRequestHandleScope.defaultUploadImageResponse() = respond(
    content = jsonSerialization.encodeToString(
        String.serializer(),
        dummyImageUrls.first()
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


fun MockRequestHandleScope.defaultProductByIdResponse(productId: String = createdProductResponseId) =
    respond(
        content = jsonSerialization.encodeToString(ProductDto.serializer(), productDto1),
        status = HttpStatusCode.OK,
        headers = jsonHeaders
    )

fun createProductHttpClient(
    uploadImageResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    paginatedResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadImagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productByIdResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    updateResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteImagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productDetailsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    toggleFavoriteResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): DukanApi {
    val dukanId = "10"
    val httpClient = HttpClient(MockEngine { request ->

        when {
            request.url.encodedPath == "/dukan/product/create" -> createResponse?.invoke(this)
                ?: defaultCreateProductResponse()

            request.url.encodedPath == "/dukan/product" -> paginatedResponse?.invoke(this)
                ?: defaultPaginatedProductResponse()

            request.url.encodedPath.startsWith("/dukan/product/images/") &&
                    request.url.encodedPath.endsWith("/delete") -> deleteImagesResponse?.invoke(this)
                ?: respond("", HttpStatusCode.OK, jsonHeaders)

            request.url.encodedPath.startsWith("/dukan/product/images/") -> uploadImagesResponse?.invoke(
                this
            )
                ?: defaultImagesUploadResponse()

            request.url.encodedPath == "/dukan/product/$createdProductResponseId" -> productDetailsResponse?.invoke(
                this
            )
                ?: defaultProductDetailsResponse()

            request.url.encodedPath.matches(Regex("/dukan/product/[^/]+/image"))
                    && request.method.value == "POST" -> uploadImageResponse?.invoke(this)
                ?: defaultUploadImageResponse()


            request.url.encodedPath.matches(Regex("/dukan/product/[^/]+$")) &&
                    request.method.value == "GET" -> productByIdResponse?.invoke(this)
                ?: defaultProductByIdResponse()

            request.url.encodedPath.matches(Regex("/dukan/product/[^/]+$")) &&
                    request.method.value == "PUT" -> updateResponse?.invoke(this)
                ?: respond("", HttpStatusCode.OK, jsonHeaders)

            request.url.encodedPath.matches(Regex("/dukan/product/[^/]+$")) &&
                    request.method.value == "DELETE" -> deleteResponse?.invoke(this)
                ?: respond("", HttpStatusCode.OK, jsonHeaders)

            request.url.encodedPath.matches(Regex("/dukan/product/[^/]+/favorite")) &&
                    request.method.value == "POST" -> toggleFavoriteResponse?.invoke(this)
                ?: respond("", HttpStatusCode.OK, jsonHeaders)

            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
    return MockDukanApiClient(httpClient)
}


fun createProductRepository(
    uploadImageResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    paginatedResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadImagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productDetailsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productByIdResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    updateResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteImagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    toggleFavoriteResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): DukanProductRepositoryImpl {
    return DukanProductRepositoryImpl(
        client = createProductHttpClient(
            uploadImageResponse = uploadImageResponse,
            createResponse = createResponse,
            paginatedResponse = paginatedResponse,
            uploadImagesResponse = uploadImagesResponse,
            productDetailsResponse = productDetailsResponse,
            productByIdResponse = productByIdResponse,
            updateResponse = updateResponse,
            deleteResponse = deleteResponse,
            deleteImagesResponse = deleteImagesResponse,
            toggleFavoriteResponse = toggleFavoriteResponse
        )
    )
}