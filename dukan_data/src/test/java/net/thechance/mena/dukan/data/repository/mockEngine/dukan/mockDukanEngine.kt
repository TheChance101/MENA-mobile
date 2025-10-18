package net.thechance.mena.dukan.data.repository.mockEngine.dukan

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
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
import net.thechance.mena.dukan.data.repository.DukanRepositoryImpl
import net.thechance.mena.dukan.data.repository.ShelfRepositoryImpl
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryDto
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryResponse
import net.thechance.mena.dukan.data.repository.dto.DukanColorDto
import net.thechance.mena.dukan.data.repository.dto.DukanColorsResponse
import net.thechance.mena.dukan.data.repository.dto.DukanDetailsDto
import net.thechance.mena.dukan.data.repository.dto.DukanNameResponse
import net.thechance.mena.dukan.data.repository.dto.MyDukanStatusDto
import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.data.repository.dto.ShelfDto
import net.thechance.mena.dukan.data.repository.mockEngine.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.jsonSerialization
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
        MyDukanStatusDto("PENDING", "Active")
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

fun MockRequestHandleScope.defaultDeleteShelfResponse() = respond(
    content = "",
    status = HttpStatusCode.NoContent,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultShelvesResponse() = respond(
    content = jsonSerialization.encodeToString(
        ListSerializer(ShelfDto.serializer()),
        listOf(
            ShelfDto("1", "Shelf 1"),
            ShelfDto("2", "Shelf 2"),
            ShelfDto("3", "Shelf 3")
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultPagedShelvesResponse() = respond(
    content = jsonSerialization.encodeToString(
        PageResponseDto.serializer(ShelfDto.serializer()),
        PageResponseDto(
            content = listOf(
                ShelfDto("1", "Shelf 1"),
                ShelfDto("2", "Shelf 2")
            ),
            number = 0,
            size = 2,
            totalPages = 5,
            totalElements = 10,
            first = true,
            last = false
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultDukanDetailsResponse() = respond(
    content = jsonSerialization.encodeToString(
        DukanDetailsDto.serializer(),
        DukanDetailsDto(
            id = "dukan123",
            ownerId = "owner456",
            name = "Test Dukan",
            imageUrl = "http://example.com/image.png",
            address = "123 Test St, Cairo, Egypt",
            color = DukanColorDto("Red", "#FF0000"),
            style = "WIDE_IMAGE",
            latitude = 30.0444,
            longitude = 31.2357
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun createDukanHttpClient(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    stylesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    categoriesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    colorsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    statusResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    nameResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    shelvesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    pagedShelvesResponse: (suspend MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData)? = null,
    dukanDetailsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    val shelfId = "1"
    val dukanId = "dukan123"

    return HttpClient(MockEngine { request ->
        when (request.url.encodedPath) {
            "/dukan/create" -> createResponse?.invoke(this) ?: defaultCreateResponse()
            "/dukan/shelf/create" -> createResponse?.invoke(this) ?: defaultCreateResponse()
            "/dukan/shelf" -> shelvesResponse?.invoke(this) ?: defaultShelvesResponse()
            "/dukan/styles" -> stylesResponse?.invoke(this) ?: defaultStylesResponse()
            "/dukan/categories" -> categoriesResponse?.invoke(this) ?: defaultCategoriesResponse()
            "/dukan/colors" -> colorsResponse?.invoke(this) ?: defaultColorsResponse()
            "/dukan/statues" -> statusResponse?.invoke(this) ?: defaultStatusResponse()
            "/dukan/image" -> uploadResponse?.invoke(this) ?: defaultUploadResponse()
            "/dukan/available" -> nameResponse?.invoke(this) ?: defaultNameAvailableResponse(false)
            "/dukan/shelf/$shelfId" -> deleteResponse?.invoke(this) ?: defaultDeleteShelfResponse()
            "/dukan/shelf/$dukanId" ->
                pagedShelvesResponse?.invoke(this, request) ?: defaultPagedShelvesResponse()
            "/dukan/$dukanId" -> dukanDetailsResponse?.invoke(this) ?: defaultDukanDetailsResponse()
            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }

}

fun createShelfRepository(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteShelfResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    shelvesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    pagedShelvesResponse: (suspend MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData)? = null,
): ShelfRepositoryImpl {
    return ShelfRepositoryImpl(
        client = createDukanHttpClient(
            createResponse = createResponse,
            deleteResponse = deleteShelfResponse,
            shelvesResponse = shelvesResponse,
            pagedShelvesResponse = pagedShelvesResponse
        )
    )
}

fun createDukanRepository(
    createResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    stylesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    categoriesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    colorsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    statusResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    uploadResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    nameResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    dukanDetailsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): DukanRepositoryImpl {
    return DukanRepositoryImpl(
        client = createDukanHttpClient(
            createResponse,
            stylesResponse,
            categoriesResponse,
            colorsResponse,
            statusResponse,
            uploadResponse,
            nameResponse,
            dukanDetailsResponse = dukanDetailsResponse
        ),
        locationService = LocationService(FakeAddressesRepository())
    )
}

@OptIn(ExperimentalUuidApi::class)
private class FakeAddressesRepository : AddressesRepository {
    override suspend fun createAddress(address: Address) {}
    override suspend fun editAddress(address: Address) {}
    override suspend fun getUserAddresses(): List<Address> {
        return listOf(
            Address(
                latitude = 30.0444,
                longitude = 31.2357,
                addressLine = "Main Street, Cairo, Egypt",
                addressType = "Home",
                otherAddressType = null,
                isActive = true
            ),
            Address(
                latitude = 30.0419,
                longitude = 31.2357,
                addressLine = "Tahrir Square, Cairo, Egypt",
                addressType = "Work",
                otherAddressType = null,
                isActive = false
            )
        )
    }

    override suspend fun deleteAddress(addressId: Uuid) {}
}
