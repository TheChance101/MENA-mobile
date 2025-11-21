@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ProductSearchDto(
    @SerialName("id")
    val id: Uuid,

    @SerialName("name")
    val name: String,

    @SerialName("dukanName")
    val dukanName: String,

    @SerialName("dukanId")
    val dukanId: Uuid,

    @SerialName("price")
    val price: Double,

    @SerialName("mainImageUrl")
    val mainImageUrl: String,

    @SerialName("isFavorite")
    val isFavorite: Boolean,
    @SerialName("isOutOfStock")
    val isOutOfStock: Boolean = false
)
