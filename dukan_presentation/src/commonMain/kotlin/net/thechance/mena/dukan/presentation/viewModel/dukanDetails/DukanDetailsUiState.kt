package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import net.thechance.mena.dukan.presentation.util.pagination.PagingData

data class DukanDetailsUiState(
    val dukanInfo: DukanInfo = DukanInfo(),
    val isDukanInfoLoading: Boolean = true,
    val bestSellingProducts: PagingData<ProductUiState> = PagingData(),
    val shelves: PagingData<ShelfUiState> = PagingData(),
    val shelvesState: ShelvesState = ShelvesState.LOADING,
    val productsShelf: PagingData<ProductUiState> = PagingData(),
    val productsState: ProductsState = ProductsState.LOADING,
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

    enum class ShelvesState {
        LOADING,
        LOADED,
        EMPTY
    }

    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val price: Double = 0.0,
        val description: String = "",
        val inCartQuantity: Int = 0
    )

    enum class ProductsState {
        LOADING,
        LOADED,
        EMPTY
    }
}