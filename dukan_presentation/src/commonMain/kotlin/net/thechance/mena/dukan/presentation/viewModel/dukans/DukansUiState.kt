package net.thechance.mena.dukan.presentation.viewModel.dukans

import net.thechance.mena.dukan.presentation.util.pagination.PagingData

data class DukansUiState(
    val dukans: PagingData<DukanUiState> = PagingData(),
    val dukansState: DukansState = DukansState.LOADING,
    val categoryId: String = "",
    val categoryTitle: String = ""
)

enum class DukansState {
    LOADING,
    LOADED,
    EMPTY
}

data class DukanUiState(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isFavorite: Boolean = false
)
