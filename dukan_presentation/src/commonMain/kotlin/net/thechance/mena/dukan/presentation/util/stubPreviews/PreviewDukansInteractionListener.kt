package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukans.DukanUiState
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansInteractionListener

object PreviewDukansInteractionListener : DukansInteractionListener {
    override fun onBackClick() {}
    override fun onDukanClick(dukan: DukanUiState) {}
    override fun onFavoriteClick(dukan: DukanUiState) {}
}
