package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfInteractionListener

object PreviewCreateShelfInteractionListener : CreateShelfInteractionListener {
    override fun onTitleChanged(shelfTitle: String) {}
    override fun onBackClicked() {}
    override fun onCreateClicked() {}
    override fun onDismissSnackBar() {}
}