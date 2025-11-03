package net.thechance.mena.dukan.presentation.viewModel.productDetails

data class ProductDetailsUiState(
    val product: ProductInfo = ProductInfo(),
    val isLoading: Boolean = true,
    val errorState: Exception? = null,
    val inCartQuantity: Int = 0,
    val isFavorite: Boolean = false,
    val selectedImageUrl: String = ""
) {
    data class ProductInfo(
        val id: String = "",
        val name: String = "",
        val price: Double = 0.0,
        val description: String = "",
        val images: List<String> = emptyList()
    )
}