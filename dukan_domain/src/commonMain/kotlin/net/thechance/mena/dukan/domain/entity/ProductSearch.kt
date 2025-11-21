@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ProductSearch(
    val id: Uuid,
    val name: String,
    val dukanName: String,
    val dukanId: Uuid,
    val imageUrl: String,
    val price: Double,
    val isOutOfStock: Boolean
)