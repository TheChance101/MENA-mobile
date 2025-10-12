package net.thechance.mena.dukan.presentation.viewModel.dukans

interface DukansInteractionListener {
    fun onBackClick()
    fun onDukanClick(dukan: DukanUiState)
    fun onFavoriteClick(dukan: DukanUiState)
}