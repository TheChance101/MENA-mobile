package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

interface CategoryDukansInteractionListener {
    fun onBackClick()
    fun onDukanClick(dukan: CategoryDukansUiState.DukanUiState)
    fun onFavoriteClick(dukan: CategoryDukansUiState.DukanUiState)
}