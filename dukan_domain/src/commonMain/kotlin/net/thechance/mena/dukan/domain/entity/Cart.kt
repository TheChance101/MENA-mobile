package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Cart(
    val id: Uuid,
    val totalPriceBeforeDiscount: Double,
    val totalPriceAfterDiscount: Double,
    val discount: Double
)