package net.thechance.mena.dukan.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductCartQuantityRequest(
    @SerialName("dukanId")
    val dukanId: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int
)