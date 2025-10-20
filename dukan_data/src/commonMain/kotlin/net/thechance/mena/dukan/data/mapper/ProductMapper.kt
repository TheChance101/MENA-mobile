package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.product.CreateProductRequest
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CreateProductParams
import kotlin.uuid.ExperimentalUuidApi

fun CreateProductParams.toCreateProductRequest(): CreateProductRequest {
    return CreateProductRequest(
        name = name,
        description = description,
        price = price,
        shelfId = shelfId
    )
}

@OptIn(ExperimentalUuidApi::class)
fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrls = imageUrls,
    createdAt = createdAt
)