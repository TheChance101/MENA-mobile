package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

interface CategoryDukansInteractionListener {
    fun onBackClicked()
    fun onDukanClicked(dukan: CategoryDukansUiState.DukanUiState)
    fun onFavoriteDukanClicked(dukanId: String)
    fun onRetryClicked()
    fun onSearchChanged(query: String)
    fun onClearSearchClicked()
    fun onSnackBarDismissed()
    fun onSearchIconClick()
}