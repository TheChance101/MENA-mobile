package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemotePaginationResponse<T>(
    @SerialName("pageNumber")
    val pageNumber : Int? = null,
    @SerialName("results")
    val results: List<T>? = null,
    @SerialName("totalResults")
    val totalResults: Int? = null
)
