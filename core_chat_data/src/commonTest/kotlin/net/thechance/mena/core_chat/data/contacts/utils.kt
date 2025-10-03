@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bilalazzam.contacts_provider.ContactsProvider
import dev.mokkery.mock
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
import net.thechance.mena.core_chat.data.chat.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.chat.dto.ChatDto
import net.thechance.mena.core_chat.data.chat.dto.MessageDto
import net.thechance.mena.core_chat.data.chat.utils.WebSocketManager
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.contacts.fakes.sampleContactDto
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.CHAT_HISTORY_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.uuid.ExperimentalUuidApi

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

fun MockRequestHandleScope.defaultChatHistoryResponse() = respond(
    content = jsonSerialization.encodeToString(
        PagedDataDto.serializer(MessageDto.serializer()),
        PagedDataDto(
            data = listOf(
                MessageDto(
                    id = "9a629aaa-8907-4dc6-ac33-f79fef7b4251",
                    senderId = "9a629aaa-8907-4dc6-ac33-f79fef7b4251",
                    chatId = "9a629aaa-8907-4dc6-ac33-f79fef7b4251",
                    text = "Hello from history",
                    sendAt = "2025-10-01T12:00:00Z",
                    isRead = false
                )
            ),
            pageNumber = 0,
            pageSize = 20,
            totalItems = 1,
            totalPages = 1
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultChatResponse() = respond(
    content = jsonSerialization.encodeToString(
        ChatDto.serializer(),
        ChatDto(
            id = "9a629aaa-8907-4dc6-ac33-f79fef7b4251",
            name = "Test Chat",
            imageUrl = null,
            requesterId = "9a629aaa-8907-4dc6-ac33-f79fef7b4251"
        )
    ),
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

fun createChatRepository(
    httpClient: HttpClient? = null,
    webSocketManager: WebSocketManager = mock(),
    authenticationRepository: AuthenticationRepository = mock<AuthenticationRepository>(),
    chatHistoryResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    baseUrl: String = ""
): ChatRepositoryImpl {
    val defaultClient = createHttpClient(
        chatHistoryResponse = chatHistoryResponse,
        chatResponse = chatResponse
    )
    return ChatRepositoryImpl(
        client = httpClient ?: defaultClient,
        webSocketManager = webSocketManager,
        authenticationRepository = authenticationRepository,
        json = jsonSerialization,
        baseUrl = baseUrl,
    )
}


fun createHttpClient(
    contactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    syncContactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatHistoryResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    val engine = MockEngine { request ->
        when (request.url.encodedPath) {
            CONTACTS_ENDPOINT -> contactsResponse?.invoke(this) ?: defaultContactsResponse()

            SYNC_CONTACTS_ENDPOINT -> syncContactsResponse?.invoke(this) ?: defaultSyncContactsResponse()

            CHAT_HISTORY_ENDPOINT -> chatHistoryResponse?.invoke(this) ?: defaultChatHistoryResponse()

            CHAT_ENDPOINT -> chatResponse?.invoke(this) ?: defaultChatResponse()

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