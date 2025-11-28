@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SearchUiState(
    val snackBarUiState: SnackBarUiState? = null,
    val searchContentState: SearchContentState = SearchContentState.Idle,
    val searchQuery: String = "",
    val dukanPagingFlow: Flow<PagingData<DukanUiState>> = emptyFlow(),
    val productPagingFlow: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val userSelectionSearchList : UserSelectionSearchList = UserSelectionSearchList.Dukans,
){
    enum class SearchContentState {
        Idle,
        Complete,
        NoInternet,
    }
    enum class UserSelectionSearchList {
        Dukans,
        Products
    }
    data class DukanUiState(
        val id: Uuid,
        val title:String,
        val imageUrl: String,
        val isFavorite: Boolean,
    )
    data class ProductUiState(
        val id: Uuid,
        val name: String,
        val isOutOfStock: Boolean,
        val imageUrl: String,
        val dukanName: String,
        val dukanId:Uuid,
        val price: Double,
    )
}
