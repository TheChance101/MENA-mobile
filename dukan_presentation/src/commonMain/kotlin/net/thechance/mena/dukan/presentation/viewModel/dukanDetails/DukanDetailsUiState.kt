package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class DukanDetailsUiState(
    val dukanInfo: DukanInfo = DukanInfo(),
    val isDukanInfoLoading: Boolean = true,
    val bestSellingProducts: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val shelves: Flow<PagingData<ShelfUiState>> = emptyFlow(),
    val productsShelf: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val shelfIdSelected: String? = null
) {
    data class DukanInfo(
        val name: String = "",
        val imageUrl: String = "",
        val style: Style = Style.NO_IMAGE,
        val color: Long = 0L,
        val coordinates: Coordinates = Coordinates(),
    )

    data class Coordinates(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }

    data class ShelfUiState(
        val id: String = "",
        val name: String = "",
        val products: List<ProductUiState> = emptyList(),
        val showProductQuantity: Boolean = false,
        val isProductsLoaded: Boolean = false
    )


    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val price: Double = 0.0,
        val description: String = "",
        val inCartQuantity: Int = 0
    )
}