package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.product.CreateProductRequest
import net.thechance.mena.dukan.data.repository.dto.product.ProductDto
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.util.CreateProductParams

fun  CreateProductParams.toCreateProductRequest(): CreateProductRequest {
    return CreateProductRequest(
        name = name,
        description = description,
        price = price,
        shelfId = shelfId
    )
}

fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrls = imageUrls,
    createdAt = createdAt
)