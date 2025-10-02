package net.thechance.mena.dukan.data.repository.mockEngine.product

import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.data.repository.dto.product.ProductDto
import net.thechance.mena.dukan.data.repository.mapper.toDomain
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.util.PagedResult


val createdProductResponseId = "jdoiejdfioewj3229048jsdfjfioewsdfio"
val demoShelfID = "shelf-123"
val productDto1 = ProductDto(

    id = "p1",
    name = "Demo Product 1",
    shelfId = demoShelfID,
    price = 9.99,
    description = "This is a demo product",
    imageUrls = listOf("https://picsum.photos/200/200?random=1","https://picsum.photos/200/200?random=2"),
    createdAt = "2025-09-26T15:26:41.300823Z"
)


val productDto2 = ProductDto(
    id = "p2",
    name = "Demo Product 2",
    shelfId = demoShelfID,
    price = 19.99,
    description = "Another demo product",
    imageUrls = listOf("https://picsum.photos/200/200?random=1","https://picsum.photos/200/200?random=2"),
    createdAt = "2025-09-26T15:26:41.300823Z"
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