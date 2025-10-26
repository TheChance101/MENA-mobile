package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState

object PreviewCategoryDukansInteractionListener : CategoryDukansInteractionListener {
    override fun onBackClicked() {}
    override fun onDukanClicked(dukan: DukanUiState) {}
    override fun onFavoriteClicked(dukan: DukanUiState) {}
}
