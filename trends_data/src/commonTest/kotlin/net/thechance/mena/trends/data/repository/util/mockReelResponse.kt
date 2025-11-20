package net.thechance.mena.trends.data.repository.util

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.utils.EmptyContent.status
import io.ktor.http.HttpStatusCode
import net.thechance.mena.trends.data.remote.dto.ReelDto
import net.thechance.mena.trends.data.remote.dto.ReelPathUrlsDto
import net.thechance.mena.trends.data.remote.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.remote.dto.SubmitWatchTimeRequest
import net.thechance.mena.trends.data.remote.dto.UploadReelResponse
import net.thechance.mena.trends.data.remote.dto.WatchTimeDto
import net.thechance.mena.trends.data.remote.mapper.toEntity

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
            isLiked = true
        )
    ),
    totalResults = 1
)

internal val fakeReelList = (fakeReelDtoList.results?.map(ReelDto::toEntity) ?: emptyList())

internal val fakeReelUrls = ReelPathUrlsDto(videoPath = "video.mp4", thumbnailPath = "image.jpg")

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
        ReelDto.serializer(),
        fakeReelDtoList.results?.first() ?: ReelDto()
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
        ReelPathUrlsDto.serializer(),
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
        UploadReelResponse.serializer(),
        UploadReelResponse("1")
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