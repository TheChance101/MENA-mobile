package net.thechance.mena.trends.data.mapper

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.domain.entity.Reel

internal fun ReelDto.toEntity(): Reel? {
    if (id == null || createdAt == null) return null

    return Reel(
        id = id,
        thumbnailUrl = reelImageUrl ?: "",
        videoUrl = videoUrl ?: "",
        description = description ?: "",
        likesCount = likesCount ?: 0,
        viewsCount = viewsCount ?: 0,
        createdAt = LocalDateTime.parse(createdAt),
        categories = categories?.mapNotNull { it.toEntity() } ?: emptyList()
    )
}