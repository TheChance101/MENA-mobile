package net.thechance.mena.dukan.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class CheckoutParams(
    val cartId: Uuid,
    val address: String,
    val longitude: Double,
    val latitude: Double
)