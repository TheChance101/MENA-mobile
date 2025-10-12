package net.thechance.mena.trends.data.repository.util

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode
import net.thechance.mena.trends.data.dto.CategoriesResponse
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.SubmitCategoriesRequestDto
import net.thechance.mena.trends.data.dto.UserStatusResponse

internal val mockCategories = listOf(
    CategoryDto("uuid 1", "Sport", "⚽"),
    CategoryDto("uuid 2", "tech", "🖥️")
)

internal fun MockRequestHandleScope.getAllCategoriesResponse(): HttpResponseData {
    return respond(
        content = jsonSerialization.encodeToString(
            CategoriesResponse.serializer(),
            CategoriesResponse(
                listOf(
                    CategoryDto("uuid 1", "Sport", "⚽"),
                    CategoryDto("uuid 2", "tech", "🖥️")
                )
            )
        ),
        status = HttpStatusCode.OK,
        headers = jsonHeaders
    )
}

internal fun MockRequestHandleScope.isCategoriesAlreadySelectedByUser() = respond(
    content = jsonSerialization.encodeToString(
        UserStatusResponse.serializer(),
        UserStatusResponse(true)
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.updateInterestsResponse() = respond(
    content = jsonSerialization.encodeToString(
        SubmitCategoriesRequestDto.serializer(),
        SubmitCategoriesRequestDto(listOf("uuid 1", "uuid 2"))
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)