@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.presentation.viewModel.search.SearchUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val previewDukansFlow =
    flowOf(
        PagingData.from(
            listOf(
                SearchUiState.DukanUiState(
                    id = Uuid.random(),
                    imageUrl = "https://example.com/image1.jpg",
                    title = "Puma",
                    isFavorite = false,
                ),
                SearchUiState.DukanUiState(
                    id = Uuid.random(),
                    imageUrl = "https://example.com/image2.jpg",
                    title = "Defacto",
                    isFavorite = true,
                ),
            )
        )
    )


val previewProductsFlow = flowOf(
    PagingData.from(
        listOf(
            SearchUiState.ProductUiState(
                id = Uuid.random(),
                imageUrl = "https://example.com/product1.jpg",
                name = "sport - shoes",
                dukanName = "Puma",
                dukanId = Uuid.random(),
                price = 99.99,
                isOutOfStock = false
            ),
            SearchUiState.ProductUiState(
                id = Uuid.random(),
                imageUrl = "https://example.com/product2.jpg",
                name = "Sports T-Shirt",
                dukanName = "Defacto",
                dukanId = Uuid.random(),
                price = 29.99,
                isOutOfStock = true
            ),
        )
    )
)
