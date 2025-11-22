package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class ShelfDetailsUiState(
    val shelfName: String = "",
    val dukanStyle: Style = Style.NO_IMAGE,
    val dukancolor: Long = 0L,
    val productsShelf: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val snackBarState: SnackBarUiState? = null,
    val productQuantity: Map<String, Int> = emptyMap(),
    val hasProductInCart: Boolean = false
) {
    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val basePrice: Double = 0.0,
        val finalPrice: Double = 0.0,
        val description: String = "",
        val showProductQuantity: Boolean = false,
        val inCartQuantity: Int = 1,
        val isOutOfStock: Boolean = false
    )

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }
}
