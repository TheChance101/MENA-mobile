package net.thechance.mena.dukan.data.dto.cart


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class CartDto(
    @SerialName("id")
    val id: Uuid,
    @SerialName("totalPriceBeforeDiscount")
    val totalPriceBeforeDiscount: Double,
    @SerialName("totalPriceAfterDiscount")
    val totalPriceAfterDiscount: Double,
    @SerialName("discount")
    val discount: Double
)