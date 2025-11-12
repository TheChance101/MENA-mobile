package net.thechance.mena.admin_panel.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanPagedResponse<T>(
    @SerialName("totalPages")
    val totalPages: Int? = 0,
    @SerialName("totalElements")
    val totalElements: Int? = 0,
    @SerialName("size")
    val size: Int? = 0,
    @SerialName("content")
    val content: List<T>? = listOf(),
    @SerialName("number")
    val number: Int? = 0,
    @SerialName("first")
    val first: Boolean? = false,
    @SerialName("last")
    val last: Boolean? = false,
    @SerialName("numberOfElements")
    val numberOfElements: Int? = 0,
    @SerialName("empty")
    val empty: Boolean? = false
)