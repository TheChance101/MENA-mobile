package net.thechance.mena.core_chat.data.contacts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bilalazzam.contacts_provider.ContactsProvider
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
import kotlinx.serialization.serializer
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.contacts.fakes.sampleContactDto
import net.thechance.mena.core_chat.data.network.ApiConstants.CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.identity.domain.repository.AuthenticationRepository

val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(
    HttpHeaders.ContentType,
    ContentType.Application.Json.toString()
)

inline fun <reified T> MockRequestHandleScope.mockSuccessPagedResponse(
    body: PagedDataDto<T>
): HttpResponseData {
    return respond(
        content = jsonSerialization.encodeToString(PagedDataDto.serializer(serializer<T>()), body),
        status = HttpStatusCode.OK,
        headers = jsonHeaders
    )
}

inline fun <reified T> MockRequestHandleScope.mockErrorPagedResponse(
    status: HttpStatusCode,
): HttpResponseData {
    return respond(
        content = """{"status":$status,"success":false,"message":"${status.description}"}""",
        status = status,
        headers = jsonHeaders
    )
}

fun MockRequestHandleScope.defaultContactsResponse() = respond(
    content = jsonSerialization.encodeToString(
        PagedDataDto.serializer(ContactDto.serializer()),
        PagedDataDto(
            data = listOf(
                sampleContactDto
            ),
            pageNumber = 1,
            pageSize = 10,
            totalItems = 1,
            totalPages = 1
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultSyncContactsResponse() = respond(
    content = "",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun createRepository(
    contactsProvider: ContactsProvider,
    contactsDataStore: DataStore<Preferences>,
    authenticationRepository: AuthenticationRepository,
    contactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    syncContactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): ContactsRepositoryImpl {
    return ContactsRepositoryImpl(
        client = createHttpClient(
            contactsResponse = contactsResponse,
            syncContactsResponse = syncContactsResponse
        ),
        authenticationRepository = authenticationRepository,
        contactsProvider = contactsProvider,
        dataStore = contactsDataStore
    )
}

fun createHttpClient(
    contactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    syncContactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): HttpClient {
    val engine = MockEngine { request ->
        when (request.url.encodedPath) {
            CONTACTS_ENDPOINT -> if (contactsResponse == null)
                defaultContactsResponse()
            else
                contactsResponse()

            SYNC_CONTACTS_ENDPOINT -> if (syncContactsResponse == null)
                defaultSyncContactsResponse()
            else
                syncContactsResponse()

            else -> respond(
                content = "",
                status = HttpStatusCode.BadRequest,
                headers = jsonHeaders
            )
        }
    }

    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(jsonSerialization)
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }
}