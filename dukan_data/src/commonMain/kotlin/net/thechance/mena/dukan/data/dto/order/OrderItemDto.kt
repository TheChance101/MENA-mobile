@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.dto.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class OrderItemDto(
    @SerialName("productId")
    val productId: Uuid,
    @SerialName("productName")
    val productName: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: PriceDto,
    @SerialName("imageUrl")
    val imageUrl: String?
)
