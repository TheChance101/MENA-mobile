package net.thechance.mena.admin_panel.domain.entity.dukan

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Product(
    val id: Uuid,
    val name: String,
    val finalPrice: Double,
    val basePrice: Double,
    val description: String,
    val imageUrls: List<String>,
)