package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState

object PreviewCategoryDukansInteractionListener : CategoryDukansInteractionListener {
    override fun onBackClick() {}
    override fun onDukanClick(dukan: DukanUiState) {}
    override fun onFavoriteClick(dukan: DukanUiState) {}
}
