package net.thechance.mena.faith.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    @SerialName("number")
    val currentPage: Int? = null,
    @SerialName("content")
    val items: List<T>? = emptyList(),
    @SerialName("totalPages")
    val totalPages: Int? = null,
    @SerialName("totalElements")
    val totalItems: Int? = null
)