package net.thechance.mena.trends.domain.model

data class TrendUpdates(
    val trendId: String,
    val isLiked: Boolean,
    val likesCount: Int,
    val viewsCount: Int,
    val isDeleted: Boolean
)