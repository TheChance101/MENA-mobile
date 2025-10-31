package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class DukanCartUiState(
    val dukanCartState: DukanCartState = DukanCartState.LOADED,
    val totalPrice: Double = 0.0,
    val dukanDetails: DukanDetailsUiState = DukanDetailsUiState(),
    val products: Flow<PagingData<ProductsUiState>> = emptyFlow()
) {

    data class DukanDetailsUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = ""
    )

    data class ProductsUiState(
        val id: String = "",
        val name: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val price: Double = 0.0,
        val quantity: Int = 0
    )

    enum class DukanCartState {
        LOADING,
        LOADED,
        ERROR,
        EMPTY
    }
}
