package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedDataDto<T>(
    @SerialName("data")
    val data: List<T>,
    @SerialName("pageNumber")
    val pageNumber: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("totalItems")
    val totalItems: Int,
    @SerialName("totalPages")
    val totalPages: Int
)