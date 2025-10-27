package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

interface CategoryDukansInteractionListener {
    fun onBackClicked()
    fun onDukanClicked(dukan: CategoryDukansUiState.DukanUiState)
    fun onFavoriteClicked(dukan: CategoryDukansUiState.DukanUiState)
}