@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import net.thechance.mena.dukan.domain.model.DukanPreview
import net.thechance.mena.dukan.domain.entity.ProductSearch
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val fakeDefactoDukanPaged = PagedResult(
    currentPage = 1,
    totalItems = 1,
    pageSize = 1,
    totalPages = 1,
    items = listOf(
        DukanPreview(
            id = Uuid.random(),
            name = "Defacto",
            imageUrl = "https://example.com/dukan1.jpg",
            isFavorite = false,
        ),
    )
)

val fakePumaShoesProductPaged = PagedResult(
    currentPage = 1,
    totalItems = 1,
    pageSize = 1,
    totalPages = 1,
    items = listOf(
        ProductSearch(
            id = Uuid.random(),
            name = "puma-shoes",
            imageUrl = "https://example.com/dukan1.jpg",
            dukanName = "Puma",
            dukanId = Uuid.random(),
            price = 20.0,
            isOutOfStock = false,
        ),
    )
)