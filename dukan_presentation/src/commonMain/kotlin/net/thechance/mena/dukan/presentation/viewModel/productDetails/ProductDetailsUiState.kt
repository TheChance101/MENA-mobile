package net.thechance.mena.dukan.presentation.viewModel.productDetails

import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class ProductDetailsUiState(
    val product: ProductInfo = ProductInfo(),
    val isLoading: Boolean = false,
    val isAddToCartLoading: Boolean = false,
    val errorState: Exception? = null,
    val isFavorite: Boolean = false,
    val selectedImageUrl: String = "",
    val isFirstQuantityOne: Boolean = false,
    val snackBarState: SnackBarUiState? = null,
    val hasProductInCart: Boolean = false,
    val isButtonEnable: Boolean = false,
    val dukanColor: Long = 0,
) {
    data class ProductInfo(
        val id: String = "",
        val name: String = "",
        val basePrice: Double = 0.0,
        val finalPrice: Double = 0.0,
        val description: String = "",
        val images: List<String> = emptyList(),
        val inCartQuantity: Int = 0,
        val finalProductQuantity: Int = 0,
        val isOutOfStock: Boolean = false,
    )
}