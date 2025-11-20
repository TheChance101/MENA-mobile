package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class DukanDetailsUiState(
    val dukanInfo: DukanInfo = DukanInfo(),
    val isDukanInfoLoading: Boolean = true,
    val bestSellingProducts: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val shelves: Flow<PagingData<ShelfUiState>> = emptyFlow(),
    val productsShelf: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val shelfIdSelected: String? = null,
    val dukanDetailsState: DukanDetailsState = DukanDetailsState.LOADING,
    val snackBarState: SnackBarUiState? = null,
    val error: Throwable? = null,
    val hasProductInCart: Boolean = false,
    val shelfProductsLimited: Map<String, List<ProductUiState>> = emptyMap(),
    val productQuantity: Map<String, Int> = emptyMap(),
    val isConfigurationChanges: Boolean = true
) {
    data class DukanInfo(
        val dukanId: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val isFavorite: Boolean = false,
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
        val name: String = ""
    )


    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val price: Double = 0.0,
        val description: String = "",
        val showProductQuantity: Boolean = false,
        val inCartQuantity: Int = 1,
        val isOutOfStock: Boolean = false
    )

    enum class DukanDetailsState {
        LOADING,
        LOADED,
        ERROR
    }
}