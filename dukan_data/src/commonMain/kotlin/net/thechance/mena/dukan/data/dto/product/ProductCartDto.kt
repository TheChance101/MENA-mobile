package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.mena.dukan.domain.entity.ProductCart
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ProductCartDto(
    @SerialName("productId")
    val id: Uuid,
    @SerialName("productName")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Double,
    @SerialName("imageUrl")
    val imageUrl: String,
)
@OptIn(ExperimentalUuidApi::class)
fun ProductCartDto.toProductCart() = ProductCart(
    id =id,
    name = name,
    description = description,
    quantity = quantity,
    price = price,
    imageUrl = imageUrl,
)