package net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart

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
import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.dto.order.OrderDto
import net.thechance.mena.dukan.data.dto.product.ProductCartDto
import net.thechance.mena.dukan.data.repository.CartRepositoryImpl
import net.thechance.mena.dukan.data.repository.OrderRepositoryImpl
import net.thechance.mena.dukan.data.repository.mockEngine.MockDukanApiClient
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization
import net.thechance.mena.dukan.data.util.network.DukanApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun MockRequestHandleScope.defaultAddOrUpdateProductQuantityResponse() = respond(
    content = """{}""",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultDeleteProductFromCartResponse() = respond(
    content = "",
    status = HttpStatusCode.NoContent,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultCartInfoResponse() = respond(
    content = jsonSerialization.encodeToString(
        CartDto.serializer(),
        cartDto
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultProductCartResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(ProductCartDto.serializer()),
        PageResponseDto(
            content = listOf(productCartDto1, productCartDto2),
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

fun MockRequestHandleScope.defaultCartOrdersResponse() = respond(
    content = jsonSerialization.encodeToString(
        OrderDto.serializer(),
        cartOdrerDto
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

@OptIn(ExperimentalUuidApi::class)
fun dukanCartHttpClient(
    addOrUpdateProductCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteProductFromCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    getCartInfoResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): DukanApi {
    val dukanId = "123e4567-e89b-12d3-a456-426614174003"
    val productId = "5"
    val httpClient = HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/dukan/cart/items" -> addOrUpdateProductCartResponse?.invoke(this)
                ?: defaultAddOrUpdateProductQuantityResponse()

            "/dukan/cart/$dukanId/items/$productId" -> deleteProductFromCartResponse?.invoke(this)
                ?: defaultDeleteProductFromCartResponse()

            "/dukan/cart/$dukanId/info" -> getCartInfoResponse?.invoke(this)
                ?: defaultCartInfoResponse()

            "/dukan/cart/$dukanId/items" -> productCartResponse?.invoke(this)
                ?: defaultProductCartResponse()

            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
    return MockDukanApiClient(httpClient)
}


fun dukanCartRepository(
    addOrUpdateProductCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteProductFromCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    getCartInfoResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    productCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): CartRepositoryImpl {
    return CartRepositoryImpl(
        client = dukanCartHttpClient(
            addOrUpdateProductCartResponse = addOrUpdateProductCartResponse,
            deleteProductFromCartResponse = deleteProductFromCartResponse,
            getCartInfoResponse = getCartInfoResponse,
            productCartResponse = productCartResponse,
        )
    )
}

@OptIn(ExperimentalUuidApi::class)
fun orderHttpClient(
    orderId: Uuid,
    cartOrdersResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): DukanApi {
    val httpClient = HttpClient(
        MockEngine { request ->
            when (request.url.encodedPath) {
                "/dukan/orders/$orderId" -> cartOrdersResponse?.invoke(this)
                    ?: defaultCartOrdersResponse()

                else -> respond(
                    content = "",
                    status = HttpStatusCode.BadRequest,
                    headers = jsonHeaders
                )
            }
        }
    ) {
        install(ContentNegotiation) {
            json(jsonSerialization)
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }
    return MockDukanApiClient(httpClient)
}

@OptIn(ExperimentalUuidApi::class)
fun dukanCartOrdersRepository(
    orerId: Uuid,
    cartOrdersResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): OrderRepositoryImpl {
    return OrderRepositoryImpl(
        client = orderHttpClient(
            orderId = orerId,
            cartOrdersResponse = cartOrdersResponse
        )
    )
}