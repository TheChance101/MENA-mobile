@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bilalazzam.contacts_provider.ContactsProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.thechance.mena.core_chat.data.contacts.fakes.createChatDto
import net.thechance.mena.core_chat.data.contacts.fakes.createChatSummaryDto
import net.thechance.mena.core_chat.data.contacts.fakes.createMessageDto
import net.thechance.mena.core_chat.data.contacts.fakes.sampleContactDto
import net.thechance.mena.core_chat.data.messagesender.MessageSenderFactory
import net.thechance.mena.core_chat.data.repository.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.repository.ContactsRepositoryImpl
import net.thechance.mena.core_chat.data.repository.MessageRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDao
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageDao
import net.thechance.mena.core_chat.data.source.local.database.chatSyncTime.ChatSyncTimeDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageDao
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.UserDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.faith.domain.service.QuranService
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
        content = "",
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
                createMessageDto()
            ),
            pageNumber = 1,
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
        createChatDto()
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultUserInfoResponse() = respond(
    content = jsonSerialization.encodeToString(
        serializer = UserDto.serializer(),
        value = UserDto(
            firstName = "sam",
            lastName = "smith",
            imageUrl = "url",
            phoneNumber = "7777",
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultChatSummaryResponse() = respond(
    content = jsonSerialization.encodeToString(
        PagedDataDto.serializer(ChatSummaryDto.serializer()),
        PagedDataDto(
            data = listOf(
                createChatSummaryDto()
            ),
            pageNumber = 1,
            pageSize = 20,
            totalItems = 1,
            totalPages = 1
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultUploadImagesResponse() = respond(
    content = jsonSerialization.encodeToString(
        MessageDto.serializer(),
        createMessageDto()
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultDeleteChatResponse() = respond(
    content = "",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultAudioResponse() = respond(
    content = jsonSerialization.encodeToString(
        MessageDto.serializer(),
        createMessageDto()
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.defaultAudioDownloadResponse(audioBytes: ByteArray) = respond(
    content = ByteReadChannel(audioBytes),
    status = HttpStatusCode.OK,
    headers = headersOf(HttpHeaders.ContentType, "audio/m4a")
)

fun createRepository(
    contactsProvider: ContactsProvider,
    contactsDataStore: DataStore<Preferences>,
    contactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    syncContactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): ContactsRepositoryImpl {
    return ContactsRepositoryImpl(
        client = createHttpClient(
            contactsResponse = contactsResponse,
            syncContactsResponse = syncContactsResponse
        ),
        contactsProvider = contactsProvider,
        dataStore = contactsDataStore
    )
}

fun createChatRepository(
    httpClient: HttpClient? = null,
    webSocketManager: WebSocketManager,
    dataStore: DataStore<Preferences>,
    cachedChatSummaryDao: CachedChatSummaryDao,
    cachedChatDao: CachedChatDao,
    chatHistoryResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatSummaryResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatByIdResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteChatResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null
): ChatRepositoryImpl {
    val defaultClient = createHttpClient(
        chatHistoryResponse = chatHistoryResponse,
        chatResponse = chatResponse,
        chatsSummariesResponse = chatSummaryResponse,
        chatByIdResponse = chatByIdResponse,
        deleteChatResponse = deleteChatResponse
    )
    return ChatRepositoryImpl(
        client = httpClient ?: defaultClient,
        webSocketManager = webSocketManager,
        dataStore = dataStore,
        cachedChatSummaryDao = cachedChatSummaryDao,
        cachedChatDao = cachedChatDao
    )

}

fun createMessageRepository(
    httpClient: HttpClient,
    webSocketManager: WebSocketManager,
    messageSenderFactory: MessageSenderFactory,
    pendingMessageDao: PendingMessageDao,
    cachedMessageDao: CachedMessageDao,
    quranService: QuranService,
    chatSyncTimeDao: ChatSyncTimeDao
): MessageRepositoryImpl {
    return MessageRepositoryImpl(
        webSocketManager = webSocketManager,
        pendingMessageDao = pendingMessageDao,
        chatSyncTimeDao = chatSyncTimeDao,
        client = httpClient,
        messageSenderFactory = messageSenderFactory,
        cachedMessageDao = cachedMessageDao,
        quranService = quranService,
        json = jsonSerialization
    )
}

fun createHttpClient(
    contactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    syncContactsResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatHistoryResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    imagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    audioResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    audioDownloadResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatByIdResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    chatsSummariesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    userResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteChatResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    syncLatestMessagesResponse: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    val engine = MockEngine { request ->
        val path = request.url.encodedPath
        when {
            path == CONTACTS_ENDPOINT -> contactsResponse?.invoke(this)
                ?: defaultContactsResponse()

            path == SEARCH_CONTACTS_ENDPOINT -> contactsResponse?.invoke(this)
                ?: defaultContactsResponse()

            path == SYNC_CONTACTS_ENDPOINT -> syncContactsResponse?.invoke(this)
                ?: defaultSyncContactsResponse()

            path.contains("/chat") && path.endsWith("/messages") -> chatHistoryResponse?.invoke(this)
                ?: defaultChatHistoryResponse()

            path.startsWith(DELETE_CHAT_ENDPOINT) ->
                deleteChatResponse?.invoke(this) ?: defaultDeleteChatResponse()

            path == CHATS_SUMMARIES_ENDPOINT ->
                chatsSummariesResponse?.invoke(this) ?: defaultChatSummaryResponse()

            path == CHAT_ENDPOINT ->
                chatResponse?.invoke(this) ?: defaultChatResponse()

            path.contains(IMAGES_ENDPOINT) ->
                imagesResponse?.invoke(this) ?: defaultUploadImagesResponse()

            path.contains(AUDIO_ENDPOINT) && request.method.value == "POST" ->
                audioResponse?.invoke(this) ?: defaultAudioResponse()

            path.contains(".m4a") || path.contains(".wav") || path.contains(".mp3") ->
                audioDownloadResponse?.invoke(this) ?: defaultAudioDownloadResponse(ByteArray(1024))

            path.contains(USER_ENDPOINT) ->
                userResponse?.invoke(this) ?: defaultUserInfoResponse()

            path.contains("/messages/latest") ->
                syncLatestMessagesResponse?.invoke(this) ?: defaultChatHistoryResponse()

            path.startsWith("$CHAT_ENDPOINT/") ->
                chatByIdResponse?.invoke(this) ?: defaultChatResponse()


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

fun mockClient(handler: MockRequestHandler): HttpClient {
    val mockEngine = MockEngine(handler)
    return HttpClient(mockEngine) {
        install(ContentNegotiation) { json(jsonSerialization) }
    }
}

inline fun <reified T> MockRequestHandleScope.mockRespond(
    value: T,
    status: HttpStatusCode = HttpStatusCode.OK,
    headers: Headers = jsonHeaders
) = respond(
    content = jsonSerialization.encodeToString<T>(value),
    status = status,
    headers = headers
)


private const val CONTACTS_ENDPOINT = "/chat/contacts"
private const val SYNC_CONTACTS_ENDPOINT = "/chat/contacts/sync"
private const val CHAT_ENDPOINT = "/chat"

private const val USER_ENDPOINT = "/chat/user"
private const val CHATS_SUMMARIES_ENDPOINT = "/chat/chatsSummary"
private const val IMAGES_ENDPOINT = "/chat/image"
private const val AUDIO_ENDPOINT = "/chat/audio"
private const val DELETE_CHAT_ENDPOINT = "/chat/delete"
private const val SEARCH_CONTACTS_ENDPOINT = "/chat/contacts/search"
