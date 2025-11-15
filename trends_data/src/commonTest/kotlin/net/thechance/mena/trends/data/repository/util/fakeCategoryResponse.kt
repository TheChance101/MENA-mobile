package net.thechance.mena.trends.data.repository.util

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode
import net.thechance.mena.trends.data.remote.dto.CategoryDto
import net.thechance.mena.trends.data.remote.dto.SubmitCategoriesRequestDto
import net.thechance.mena.trends.data.remote.dto.UpdateUserCategoriesResponse

internal val mockCategories = listOf(
    CategoryDto("uuid 1", "Sport", "⚽", false),
    CategoryDto("uuid 2", "tech", "🖥️", false)
)

internal fun MockRequestHandleScope.getAllCategoriesResponse(): HttpResponseData {
    return respond(
        content = jsonSerialization.encodeToString(
            listOf(
                CategoryDto("uuid 1", "Sport", "⚽", false),
                CategoryDto("uuid 2", "tech", "🖥️", false)
            )
        ),
        status = HttpStatusCode.OK,
        headers = jsonHeaders
    )
}

internal fun MockRequestHandleScope.patchUserInterestsResponse() = respond(
    content = jsonSerialization.encodeToString(
        UpdateUserCategoriesResponse.serializer(),
        UpdateUserCategoriesResponse(
            updatedCategories = listOf(
                CategoryDto("uuid2", "Sport", "⚽", true),
                CategoryDto("uuid3", "tech", "🖥️", true)
            )
        )
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