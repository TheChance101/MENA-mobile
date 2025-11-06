package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProductCart(
    val id: Uuid,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
)