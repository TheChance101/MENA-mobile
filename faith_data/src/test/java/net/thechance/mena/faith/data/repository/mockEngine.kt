package net.thechance.mena.faith.data.repository

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

val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

internal fun bookmarkRepository(
    getBookmarks: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    postBookmark: (suspend MockRequestHandleScope.(surahId: Int, ayahNumber: Int) -> HttpResponseData)? = null,
    deleteBookmark: (suspend MockRequestHandleScope.(ayahBookmarkId: Int) -> HttpResponseData)? = null,
): BookmarkRepositoryImpl {
    return BookmarkRepositoryImpl(
        quranDao = MockQuranDao(),
        httpClient = createHttpClient(
            getBookmarks,
            postBookmark,
            deleteBookmark
        )
    )
}

internal fun createHttpClient(
    getBookmarks: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    postBookmark: (suspend MockRequestHandleScope.(surahId: Int, ayahNumber: Int) -> HttpResponseData)? = null,
    deleteBookmark: (suspend MockRequestHandleScope.(ayahBookmarkId: Int) -> HttpResponseData)? = null,
): HttpClient {
    return HttpClient(MockEngine { request ->
        when {
            request.url.encodedPath == "/faith/ayah/bookmark" && request.method.value == "GET" ->
                getBookmarks?.invoke(this) ?: getBookmarksResponse()

            request.url.encodedPath == "/faith/ayah/bookmark" && request.method.value == "POST" ->
                postBookmark?.invoke(this, 1, 1) ?: postBookmarkResponse()

            request.url.encodedPath.startsWith("/faith/ayah/bookmark/") && request.method.value == "DELETE" ->
                deleteBookmark?.invoke(this, 1) ?: deleteBookmarkResponse()

            else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
}
