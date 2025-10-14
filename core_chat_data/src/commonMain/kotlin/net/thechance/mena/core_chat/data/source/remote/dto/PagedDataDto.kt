package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedDataDto<T>(
    @SerialName("data")
    val data: List<T>? = null,
    @SerialName("pageNumber")
    val pageNumber: Int? = null,
    @SerialName("pageSize")
    val pageSize: Int? = null,
    @SerialName("totalItems")
    val totalItems: Int? = null,
    @SerialName("totalPages")
    val totalPages: Int? = null
)