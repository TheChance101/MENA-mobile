package net.thechance.mena.trends.data.remote.mapper

import net.thechance.mena.trends.data.remote.dto.TrendDto
import net.thechance.mena.trends.data.remote.dto.TrendPathUrlsDto
import net.thechance.mena.trends.data.util.orFalse
import net.thechance.mena.trends.data.util.orZero
import net.thechance.mena.trends.data.util.parseDateStringOrNull
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUrls


internal fun TrendDto.toEntity(): Trend {
    return Trend(
        id = id.orEmpty(),
        thumbnailUrl = trendImageUrl.orEmpty(),
        videoUrl = videoUrl.orEmpty(),
        description = description.orEmpty(),
        likesCount = likesCount.orZero(),
        viewsCount = viewsCount.orZero(),
        createdAt = createdAt.parseDateStringOrNull(),
        userName = username,
        profileImageUrl = profilePictureUrl.orEmpty(),
        isCurrentUserOwner = isCurrentUserOwner,
        isLiked = isLiked.orFalse()
    )
}

internal fun TrendPathUrlsDto.toTrendUrls(): TrendUrls{
    return TrendUrls(
        videoUrl = videoPath.orEmpty(),
        thumbnailUrl = thumbnailPath.orEmpty()
    )
}