package net.thechance.mena.trends.data.repository.util

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
import net.thechance.mena.trends.data.repository.CategoryRepositoryImpl
import net.thechance.mena.trends.data.repository.ReelsRepositoryImpl
import net.thechance.mena.trends.data.util.NetworkConstants.CATEGORIES_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.INTERESTS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS_PATH

val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

internal fun createReelsRepository(
    getReels: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteReel: (suspend MockRequestHandleScope.(id: String) -> HttpResponseData)? = null,
    updateReel: (suspend MockRequestHandleScope.(id: String, description: String, categoryIds: List<String>) -> HttpResponseData)? = null
): ReelsRepositoryImpl {
    return ReelsRepositoryImpl(createReelsHttpClient(getReels, deleteReel,updateReel))
}

internal fun createReelsHttpClient(
    getReels: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    deleteReel: (suspend MockRequestHandleScope.(id: String) -> HttpResponseData)? = null,
    updateReel: (suspend MockRequestHandleScope.(id: String, description: String, categoryIds: List<String>) -> HttpResponseData)? = null
): HttpClient {
    return HttpClient(MockEngine { request ->
        when {
            request.url.encodedPath == "/trends/reels" && request.method.value == "GET" -> {
                getReels?.invoke(this) ?: getReelsResponse()
            }

            request.url.encodedPath == "/trends/reels/1" && request.method.value == "DELETE" -> {
                deleteReel?.invoke(this, "1") ?: deleteReelResponse("1")
            }

            request.url.encodedPath == "/trends/reels/1" && request.method.value == "PUT" -> {
                updateReel?.invoke(this, "1", "Updated description", listOf("cat1"))
                    ?: updateReelResponse("1", "Updated description", listOf("cat1"))

            }
            else -> respond(
                "", HttpStatusCode.BadRequest,
                jsonHeaders
            )
        }
    }) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
}

internal fun createCategoryRepository(
    getAllCategories: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    isCategoriesAlreadySelectedByUser: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    updateInterests: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): CategoryRepositoryImpl {
    return CategoryRepositoryImpl(
        createHttpClient(
            getAllCategories,
            isCategoriesAlreadySelectedByUser,
            updateInterests
        )
    )
}

internal fun createHttpClient(
    getAllCategories: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    isCategoriesAlreadySelectedByUser: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
    updateInterests: (suspend MockRequestHandleScope.() -> HttpResponseData)? = null,
): HttpClient {
    return HttpClient(
        MockEngine { request ->
            when (request.url.encodedPath) {
                "/$TRENDS_PATH/$CATEGORIES_ENDPOINT" -> {
                    getAllCategories?.invoke(this) ?: getAllCategoriesResponse()
                }

                "/$TRENDS_PATH/user/categories/status" -> {
                    isCategoriesAlreadySelectedByUser?.invoke(this) ?: isCategoriesAlreadySelectedByUser()
                }

                "/$TRENDS_PATH/$INTERESTS_ENDPOINT" -> updateInterests?.invoke(this) ?: updateInterestsResponse()

                else -> respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        }
    ) {
        install(ContentNegotiation) { json(jsonSerialization) }
        install(DefaultRequest) { contentType(ContentType.Application.Json) }
    }
}
