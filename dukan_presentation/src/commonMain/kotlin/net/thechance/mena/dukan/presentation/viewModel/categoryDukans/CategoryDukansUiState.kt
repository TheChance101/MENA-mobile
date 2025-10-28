package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class CategoryDukansUiState(
    val dukans: Flow<PagingData<DukanUiState>> = emptyFlow(),
    val categoryId: String = "",
    val categoryTitle: String = ""
) {
    data class DukanUiState(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val isFavorite: Boolean = false
    )
}