package net.thechance.mena.faith.data.remote.dto.bookmark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AyahBookmarkResponse(
    @SerialName("content") val ayahBookmarks: List<AyahBookmarkDto>
)
