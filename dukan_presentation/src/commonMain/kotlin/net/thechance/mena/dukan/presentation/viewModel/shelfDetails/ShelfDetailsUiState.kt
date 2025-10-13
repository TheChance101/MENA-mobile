package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import net.thechance.mena.dukan.presentation.util.pagination.PagingData

data class ShelfDetailsUiState(
    val shelfName: String = "",
    val productsShelf: PagingData<ProductUiState> = PagingData(),
    val productsState: ProductsState = ProductsState.LOADING,
) {
    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val price: Double = 0.0,
        val description: String = "",
        val showProductQuantity: Boolean = false
    )

    enum class ProductsState {
        LOADING,
        LOADED,
        EMPTY
    }
}
