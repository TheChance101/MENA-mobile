package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Product(
    val id: Uuid,
    val name: String,
    val description: String,
    val price: Price,
    val imageUrls: List<String>,
    val createdAt: String,
    val quantityInCart: Int,
    val shelfId: Uuid?,
    val isFavorite: Boolean,
    val isOutOfStock: Boolean = false
)