package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReelDto(
    @SerialName("reelId")
    val id : String? = null,
    @SerialName("thumbnailUrl")
    val reelImageUrl : String? = null,
    @SerialName("videoUrl")
    val videoUrl : String? = null,
    @SerialName("description")
    val description : String? = null,
    @SerialName("createdAt")
    val createdAt : String? = null,
    @SerialName("likesCount")
    val likesCount : Int? = null,
    @SerialName("viewsCount")
    val viewsCount : Int? = null,
    @SerialName("categories")
    val categories : List<CategoryDto>? = null
)
