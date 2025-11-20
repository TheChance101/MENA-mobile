package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.product.CreateProductRequest
import net.thechance.mena.dukan.data.dto.product.ProductCartDto
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.data.dto.product.UpdateProductRequest
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CreateProductParams
import net.thechance.mena.dukan.domain.model.UpdateProductParams
import kotlin.uuid.ExperimentalUuidApi

fun CreateProductParams.toCreateProductRequest(): CreateProductRequest {
    return CreateProductRequest(
        name = name,
        description = description,
        price = price.toRequest(),
        shelfId = shelfId
    )
}

fun UpdateProductParams.toUpdateProductRequest(): UpdateProductRequest {
    return UpdateProductRequest(
        name = name?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() },
        price = price.toRequest(),
        shelfId = shelfId?.takeIf { it.isNotBlank() },
        imageUrls = imageUrls?.takeIf { it.isNotEmpty() },
        isOutOfStock = isOutOfStock
    )
}

@OptIn(ExperimentalUuidApi::class)
fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price.toDomain(),
    imageUrls = imageUrls,
    createdAt = createdAt,
    quantityInCart = quantityInCart,
    shelfId = shelfId,
    isFavorite = isFavorite,
    isOutOfStock = isOutOfStock
)

@OptIn(ExperimentalUuidApi::class)
fun ProductCartDto.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price.toDomain(),
    imageUrls = listOf(imageUrl),
    quantityInCart = quantityInCart,
    createdAt = "",
    shelfId = null,
    isFavorite = false,
    isOutOfStock = false
)