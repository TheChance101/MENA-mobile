package net.thechance.mena.admin_panel.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    @SerialName("totalElements")
    val totalElements: Int?,
    @SerialName("page")
    val page: Int?,
    @SerialName("pageSize")
    val pageSize: Int?,
    @SerialName("totalPages")
    val totalPages: Int?,
    @SerialName("items")
    val items: List<T>?
)