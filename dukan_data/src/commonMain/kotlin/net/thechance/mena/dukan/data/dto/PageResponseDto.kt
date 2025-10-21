package net.thechance.mena.dukan.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageResponseDto<T>(
    @SerialName("content")
    val content: List<T>,

    @SerialName("number")
    val number: Int,

    @SerialName("size")
    val size: Int,

    @SerialName("totalPages")
    val totalPages: Int,

    @SerialName("totalElements")
    val totalElements: Long,

    @SerialName("first")
    val first: Boolean,

    @SerialName("last")
    val last: Boolean
)