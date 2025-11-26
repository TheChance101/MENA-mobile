package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TrendDto(
    @SerialName("trendId")
    val id: String? = null,
    @SerialName("thumbnailUrl")
    val trendImageUrl: String? = null,
    @SerialName("videoUrl")
    val videoUrl: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("likesCount")
    val likesCount: Int? = null,
    @SerialName("viewsCount")
    val viewsCount: Int? = null,
    @SerialName("isCurrentUserOwner")
    val isCurrentUserOwner: Boolean = false,
    @SerialName("username")
    val username: String = "The Chance",
    @SerialName("profilePictureUrl")
    val profilePictureUrl: String? = null,
    @SerialName("isLiked")
    val isLiked: Boolean? = null
)
