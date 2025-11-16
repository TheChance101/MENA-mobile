package net.thechance.mena.admin_panel.data.mapper.dukan

import net.thechance.mena.admin_panel.data.mapper.toUuidOrNull
import net.thechance.mena.admin_panel.data.utils.orZero
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ProductDto.toEntity() = Product(
    id = id.toUuidOrNull() ?: throw IllegalStateException("Invalid product id"),
    name = name.orEmpty(),
    finalPrice = finalPrice.orZero(),
    basePrice = basePrice.orZero(),
    description = description.orEmpty(),
    imageUrls = imageUrls.orEmpty(),
)