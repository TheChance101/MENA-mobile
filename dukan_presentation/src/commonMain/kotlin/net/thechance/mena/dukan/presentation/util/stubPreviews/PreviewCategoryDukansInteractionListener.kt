package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState

object PreviewCategoryDukansInteractionListener : CategoryDukansInteractionListener {
    override fun onBackClicked() {}
    override fun onDukanClicked(dukan: DukanUiState) {}
    override fun onFavoriteDukanClicked(dukanId: String) {}
    override fun onRetryClicked() {}
    override fun onSearchChanged(query: String) {}

    override fun onClearSearchClicked() {}
    override fun onSnackBarDismissed() {}
    override fun onSearchIconClick() {}
}
