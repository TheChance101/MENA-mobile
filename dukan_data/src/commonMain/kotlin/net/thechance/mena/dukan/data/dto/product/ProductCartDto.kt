package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ProductCartDto(
    @SerialName("productId")
    val id: Uuid,

    @SerialName("productName")
    val name: String,

    @SerialName("price")
    val price: PriceDto,

    @SerialName("description")
    val description: String,

    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("quantity")
    val quantityInCart: Int
)