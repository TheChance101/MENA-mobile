package net.thechance.mena.trends.domain.entity

data class Reel(
    val id: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val description: String,
    val likesCount: Int,
    val viewsCount: Int,
    val createdAt: String?,
    val categories: List<Category>
)