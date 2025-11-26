package net.thechance.mena.trends.domain.entity

import kotlinx.datetime.LocalDateTime

data class Trend(
    val id: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val description: String,
    val likesCount: Int,
    val viewsCount: Int,
    val createdAt: LocalDateTime?,
    val userName: String,
    val profileImageUrl: String,
    val isCurrentUserOwner: Boolean,
    val isLiked: Boolean
)