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
import net.thechance.mena.dukan.data.repository.DukanCartRepositoryImpl
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonSerialization

fun MockRequestHandleScope.defaultUpdateProductQuantityResponse() = respond(
    content = """{}""",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)


fun dukanCartHttpClient(
    dukanCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    return HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/dukan/cart/items" -> dukanCartResponse?.invoke(this)
                ?: defaultUpdateProductQuantityResponse()

            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }

}


fun defaultUpdateProductQuantityRepository(
    dukanCartResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): DukanCartRepositoryImpl {
    return DukanCartRepositoryImpl(
        client = dukanCartHttpClient(
            dukanCartResponse = dukanCartResponse
        )
    )
}