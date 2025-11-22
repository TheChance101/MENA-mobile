package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class CategoryDukansUiState(
    val dukans: Flow<PagingData<DukanUiState>> = emptyFlow(),
    val categoryId: String = "",
    val categoryTitle: String = "",
    val snackBarUiState: SnackBarUiState? = null,
    val searchQuery: String = "",
    val onSearchMode: Boolean=false
) {
    data class DukanUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val isFavorite: Boolean = false
    )
}