package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ProductDto(
    @SerialName("id")
    val id: Uuid,

    @SerialName("name")
    val name: String,

    @SerialName("shelfId")
    val shelfId: Uuid,

    @SerialName("price")
    val price: PriceDto,

    @SerialName("discount")
    val discount: Double? = null,

    @SerialName("description")
    val description: String,

    @SerialName("imageUrls")
    val imageUrls: List<String>,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("quantityInCart")
    val quantityInCart : Int,

    @SerialName("isFavorite")
    val isFavorite: Boolean = false,

    @SerialName("isOutOfStock")
    val isOutOfStock: Boolean = false
)