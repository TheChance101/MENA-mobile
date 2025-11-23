package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class DukanCartUiState(
    val cartState: CartState = CartState.LOADING,
    val dukanInfo: DukanInfoUiState = DukanInfoUiState(),
    val dukanInfoState: DukanInfoState = DukanInfoState.LOADING,
    val products: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val totalPrice: Double = 0.0,
    val snackBarState: SnackBarUiState? = null,
    val productQuantity: Map<String, Int> = emptyMap()
) {
    data class DukanInfoUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = ""
    )

    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val basePrice: Double = 0.0,
        val finalPrice: Double = 0.0,
        val quantity: Int = 0,
        val isOutOfStock: Boolean = false
    )

    enum class DukanInfoState {
        LOADING,
        LOADED
    }

    enum class CartState {
        LOADING,
        LOADED,
        NO_INTERNET
    }
}
