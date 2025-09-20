package net.thechance.mena.trends.data.repository.util

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.CategoryResponseDto
import net.thechance.mena.trends.data.dto.SubmitInterestsRequestDto

val mockCategories = listOf(
    CategoryDto("uuid 1", "Sport", "⚽"),
    CategoryDto("uuid 2", "tech", "🖥️")
)

fun MockRequestHandleScope.getAllCategoriesResponse() = respond(
    content = jsonSerialization.encodeToString(
        CategoryResponseDto.serializer(),
        CategoryResponseDto(
            listOf(
                CategoryDto("uuid 1", "Sport", "⚽"),
                CategoryDto("uuid 2", "tech", "🖥️")
            )
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

fun MockRequestHandleScope.updateInterestsResponse() = respond(
    content = jsonSerialization.encodeToString(
        SubmitInterestsRequestDto.serializer(),
        SubmitInterestsRequestDto(listOf("uuid 1", "uuid 2"))
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)