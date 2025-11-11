package net.thechance.mena.admin_panel.data.mapper.dukan

import net.thechance.mena.admin_panel.data.utils.orZero
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.domain.entity.dukan.Product

fun ProductDto.toEntity() = Product(
    id = id,
    name = name.orEmpty(),
    price = price.orZero(),
    discountedPrice = discountedPrice,
    description = description.orEmpty(),
    imageUrls = imageUrls.orEmpty(),
)