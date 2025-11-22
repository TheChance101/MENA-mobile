package net.thechance.mena.dukan.data.repository.mockEngine.search


import net.thechance.mena.dukan.data.dto.dukan.DukanSearchDto
import net.thechance.mena.dukan.data.dto.product.ProductSearchDto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
val demoSearchDukansDto = listOf(
    DukanSearchDto(
        id = Uuid.random(),
        name = "Demo Dukan 1",
        imageUrl = "https://picsum.photos/200/200?random=1",
        isFavorite = true

    ),
    DukanSearchDto(
        id = Uuid.random(),
        name = "Demo Dukan 2",
        imageUrl = "https://picsum.photos/200/200?random=1",
        isFavorite = true

    )
)

@OptIn(ExperimentalUuidApi::class)
val demoProductsDto = listOf(
    ProductSearchDto(
        id = Uuid.random(),
        name = "Demo Product 1",
        mainImageUrl = "https://picsum.photos/200/200?random=1",
        isFavorite = true,
        dukanId = Uuid.random(),
        dukanName = "Demo Dukan 1",
        price = 10.0
    ),
    ProductSearchDto(
        id = Uuid.random(),
        name = "Demo Product 2",
        mainImageUrl = "https://picsum.photos/200/200?random=1",
        isFavorite = true,
        dukanId = Uuid.random(),
        dukanName = "Demo Dukan 1",
        price = 10.0
    )
)







