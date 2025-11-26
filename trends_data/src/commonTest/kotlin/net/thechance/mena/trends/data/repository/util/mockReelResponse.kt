package net.thechance.mena.trends.data.repository.util

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import net.thechance.mena.trends.data.remote.dto.TrendDto
import net.thechance.mena.trends.data.remote.dto.TrendPathUrlsDto
import net.thechance.mena.trends.data.remote.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.remote.dto.SubmitWatchTimeRequest
import net.thechance.mena.trends.data.remote.dto.UploadTrendResponse
import net.thechance.mena.trends.data.remote.dto.WatchTimeDto
import net.thechance.mena.trends.data.remote.mapper.toEntity

internal val fakeTrendDtoList = RemotePaginationResponse(
    pageNumber = 1,
    results = listOf(
        TrendDto(
            id = "1",
            trendImageUrl = "https://example.com/reel1.jpg",
            videoUrl = "https://example.com/reel1.mp4",
            description = "Funny reel about Kotlin Multiplatform",
            createdAt = "2025-09-16T15:06:57.507394",
            likesCount = 120,
            viewsCount = 1500,
            isLiked = true
        )
    ),
    totalResults = 1
)

internal val fakeReelList = (fakeTrendDtoList.results?.map(TrendDto::toEntity) ?: emptyList())

internal val fakeReelUrls = TrendPathUrlsDto(videoPath = "video.mp4", thumbnailPath = "image.jpg")

internal fun MockRequestHandleScope.getReelsResponse(
    reels: List<TrendDto> = fakeTrendDtoList.results ?: emptyList()
) = respond(
    content = jsonSerialization.encodeToString(
        RemotePaginationResponse.serializer(TrendDto.serializer()),
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

internal fun MockRequestHandleScope.uploadReelThumbnailResponse(
    status: HttpStatusCode = HttpStatusCode.OK
) = respond(
    content = "",
    status = status,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.toggleLikeReelResponse(
    status: HttpStatusCode = HttpStatusCode.OK
) = respond(
    content = jsonSerialization.encodeToString(
        TrendDto.serializer(),
        fakeTrendDtoList.results?.first() ?: TrendDto()
    ),
    status = status,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.addViewReelResponse(
) = respond(
    content = "",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.getReelUrlsResponse(
) = respond(
    content = jsonSerialization.encodeToString(
        TrendPathUrlsDto.serializer(),
        fakeReelUrls
    ),
    status = HttpStatusCode.OK,
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
    content = jsonSerialization.encodeToString(
        UploadTrendResponse.serializer(),
        UploadTrendResponse("1")
    ),
    status = status,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.sendEngagements() = respond(
    content = jsonSerialization.encodeToString(
        SubmitWatchTimeRequest.serializer(),
        SubmitWatchTimeRequest(
            userId = "uuid",
            watchTimes = listOf(
                WatchTimeDto(
                    trendId = "uuid",
                    videoDuration = 1000,
                    watchStartTimeStamp = "start",
                    watchEndTimeStamp = "end",
                    percentWatched = 10.0
                )
            )
        )
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)