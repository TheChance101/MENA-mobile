@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class CheckoutRequest(
    @SerialName("cartId")
    val cartId: Uuid,

    @SerialName("address")
    val address: String,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("latitude")
    val latitude: Double
)
