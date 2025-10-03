package net.thechance.mena.faith.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    @SerialName("number")
    val currentPage: Int,
    @SerialName("content")
    val items: List<T>,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("totalElements")
    val totalItems: Int
)