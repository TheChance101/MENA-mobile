package net.thechance.mena.faith.data.remote.model.bookmark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddBookmarkRequest(
    @SerialName("surahId")
    val surahId: Int,
    @SerialName("ayahNumber")
    val ayahNumber: Int
)