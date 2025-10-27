package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ShelfDetailsUiState(
    val shelfName: String = "",
    val dukanStyle: Style = Style.NO_IMAGE,
    val dukancolor: Long = 0L,
    val productsShelf: Flow<PagingData<ProductUiState>> = emptyFlow(),
) {
    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val price: Double = 0.0,
        val description: String = "",
        val inCartQuantity: Int = 0
    )
    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }
}
