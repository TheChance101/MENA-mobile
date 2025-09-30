package net.thechance.mena.trends.data.repository.util

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.mapper.toEntity

internal val fakeReelDtoList = RemotePaginationResponse(
    pageNumber = 1,
    results = listOf(
        ReelDto(
            id = "1",
            reelImageUrl = "https://example.com/reel1.jpg",
            videoUrl = "https://example.com/reel1.mp4",
            description = "Funny reel about Kotlin Multiplatform",
            createdAt = "2025-09-16T15:06:57.507394",
            likesCount = 120,
            viewsCount = 1500,
            categories = listOf(
                CategoryDto(id = "1", name = "Comedy", emoji = "😂"),
                CategoryDto(id = "2", name = "Tech", emoji = "💻")
            )
        )
    ),
    totalResults = 1
)

internal val fakeReelList = (fakeReelDtoList.results?.map(ReelDto::toEntity) ?: emptyList())

internal fun MockRequestHandleScope.getReelsResponse(
    reels: List<ReelDto> = fakeReelDtoList.results ?: emptyList()
) = respond(
    content = jsonSerialization.encodeToString(
        RemotePaginationResponse.serializer(ReelDto.serializer()),
        RemotePaginationResponse(
            pageNumber = 1,
            results = reels,
            totalResults = reels.size
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.deleteReelResponse(
    status: HttpStatusCode = HttpStatusCode.OK
) = respond(
    content = "",
    status = status,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.updateReelResponse(
    id: String,
    description: String,
    categoryIds: List<String>,
    status: HttpStatusCode = HttpStatusCode.OK
) = respond(
    content = "",
    status = status,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.uploadReelResponse(
    status: HttpStatusCode = HttpStatusCode.OK
) = respond(
    content = "",
    status = status,
    headers = jsonHeaders
)