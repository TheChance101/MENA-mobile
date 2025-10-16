package net.thechance.mena.trends.data.mapper

import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.util.orZero
import net.thechance.mena.trends.data.util.parseDateStringOrNull
import net.thechance.mena.trends.domain.entity.Reel


internal fun ReelDto.toEntity(): Reel {
    return Reel(
        id = id.orEmpty(),
        thumbnailUrl = reelImageUrl.orEmpty(),
        videoUrl = videoUrl.orEmpty(),
        description = description.orEmpty(),
        likesCount = likesCount.orZero(),
        viewsCount = viewsCount.orZero(),
        createdAt = createdAt.parseDateStringOrNull(),
        userName = username,
        profileImageUrl = profilePictureUrl.orEmpty(),
        isCurrentUserOwner = isCurrentUserOwner,
        categories = categories?.mapNotNull { it.toEntity() }.orEmpty()
    )
}