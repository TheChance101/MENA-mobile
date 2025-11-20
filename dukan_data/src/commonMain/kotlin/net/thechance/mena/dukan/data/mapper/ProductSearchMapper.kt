@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.product.ProductSearchDto
import net.thechance.mena.dukan.domain.entity.ProductSearch
import kotlin.uuid.ExperimentalUuidApi


fun ProductSearchDto.toDomain(): ProductSearch = ProductSearch(
    id = id,
    name = name,
    dukanName = dukanName,
    dukanId = dukanId,
    price = price,
    imageUrl = mainImageUrl,
    isOutOfStock = isOutOfStock
)