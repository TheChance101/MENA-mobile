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
import net.thechance.mena.dukan.data.repository.CartRepositoryImpl
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization

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

fun dukanCartHttpClient(
    addOrUpdateProductCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteProductFromCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    val dukanId = "10"
    val productId = "5"
    return HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/dukan/cart/items" -> addOrUpdateProductCartResponse?.invoke(this) ?: defaultAddOrUpdateProductQuantityResponse()
            "/dukan/cart/$dukanId/items/$productId" -> deleteProductFromCartResponse?.invoke(this) ?:defaultDeleteProductFromCartResponse()
            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }

}


fun dukanCartRepository(
    addOrUpdateProductCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteProductFromCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): CartRepositoryImpl {
    return CartRepositoryImpl(
        client = dukanCartHttpClient(
            addOrUpdateProductCartResponse = addOrUpdateProductCartResponse,
            deleteProductFromCartResponse = deleteProductFromCartResponse
        )
    )
}