package net.thechance.mena.dukan.data.repository.mockEngine.product


import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.product.PriceDto
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


val createdProductResponseId = "jdoiejdfioewj3229048jsdfjfioewsdfio"

@OptIn(ExperimentalUuidApi::class)
val demoShelfID = Uuid.random()

@OptIn(ExperimentalUuidApi::class)
val productDto1 = ProductDto(

    id = Uuid.random(),
    name = "Demo Product 1",
    shelfId = demoShelfID,
    price = PriceDto(
        base = 9.99,
        final = 9.99
    ),
    description = "This is a demo product",
    imageUrls = listOf(
        "https://picsum.photos/200/200?random=1",
        "https://picsum.photos/200/200?random=2"
    ),
    createdAt = "2025-09-26T15:26:41.300823Z",
    quantityInCart = 10,
    isFavorite = true
)


@OptIn(ExperimentalUuidApi::class)
val productDto2 = ProductDto(
    id = Uuid.random(),
    name = "Demo Product 2",
    shelfId = demoShelfID,
    price = PriceDto(
        base = 19.99,
        final = 19.99
    ),
    description = "Another demo product",
    imageUrls = listOf(
        "https://picsum.photos/200/200?random=1",
        "https://picsum.photos/200/200?random=2"
    ),
    createdAt = "2025-09-26T15:26:41.300823Z",
    quantityInCart = 10,
    isFavorite = false

)

val productDtos = listOf(
    productDto1,
    productDto2
)


val demoPagedResult: PagedResult<Product> = PageResponseDto(
    content = productDtos,
    number = 0,
    size = 2,
    totalPages = 1,
    totalElements = 2,
    first = true,
    last = true
).toDomain { it.toDomain() }


val dummyImageUrls = listOf(
    "http://example.com/image1.jpg",
    "http://example.com/image2.jpg"
)