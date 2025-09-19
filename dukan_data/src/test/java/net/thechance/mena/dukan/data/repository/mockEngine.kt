package net.thechance.mena.dukan.data.repository

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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryDto
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryResponse
import net.thechance.mena.dukan.data.repository.dto.DukanColorDto
import net.thechance.mena.dukan.data.repository.dto.DukanColorsResponse
import net.thechance.mena.dukan.data.repository.dto.DukanNameResponse
import net.thechance.mena.dukan.data.repository.dto.MyDukanStatusDto

val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())


fun MockRequestHandleScope.defaultCreateResponse() = respond(
    content = """{}""",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultStylesResponse() = respond(
    content = jsonSerialization.encodeToString(
        ListSerializer(String.serializer()),
        listOf("WIDE_IMAGE", "NO_IMAGE")
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultCategoriesResponse() = respond(
    content = jsonSerialization.encodeToString(
        DukanCategoryResponse.serializer(),
        DukanCategoryResponse(
            listOf(
                DukanCategoryDto("1", "Category 1", ""),
                DukanCategoryDto("2", "Category 2", ""),
                DukanCategoryDto("3", "Category 3", "")
            )
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultColorsResponse() = respond(
    content = jsonSerialization.encodeToString(
        DukanColorsResponse.serializer(),
        DukanColorsResponse(
            listOf(
                DukanColorDto("Red", "#FF0000"),
                DukanColorDto("Green", "#00FF00"),
                DukanColorDto("Blue", "#0000FF")
            )
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultStatusResponse() = respond(
    content = jsonSerialization.encodeToString(
        MyDukanStatusDto.serializer(),
        MyDukanStatusDto("ACTIVE", "Active")
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultUploadResponse() = respond(
    content = "https://cdn.example.com/dukan/image.png",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultNameAvailableResponse(isTaken: Boolean) = respond(
    content = jsonSerialization.encodeToString(
        DukanNameResponse.serializer(),
        DukanNameResponse(available = !isTaken)
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)


fun createHttpClient(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    stylesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    categoriesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    colorsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    statusResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    nameResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    return HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/dukan/create" -> createResponse?.invoke(this) ?: defaultCreateResponse()
            "/dukan/styles" -> stylesResponse?.invoke(this) ?: defaultStylesResponse()
            "/dukan/categories" -> categoriesResponse?.invoke(this) ?: defaultCategoriesResponse()
            "/dukan/colors" -> colorsResponse?.invoke(this) ?: defaultColorsResponse()
            "/dukan/statues" -> statusResponse?.invoke(this) ?: defaultStatusResponse()
            "/dukan/image" -> uploadResponse?.invoke(this) ?: defaultUploadResponse()
            "/dukan/available" -> nameResponse?.invoke(this) ?: defaultNameAvailableResponse(false)
            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
}


fun createDukanRepository(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    stylesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    categoriesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    colorsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    statusResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    nameResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): DukanRepositoryImpl {
    return DukanRepositoryImpl(
        client = createHttpClient(
            createResponse,
            stylesResponse,
            categoriesResponse,
            colorsResponse,
            statusResponse,
            uploadResponse,
            nameResponse
        )
    )
}