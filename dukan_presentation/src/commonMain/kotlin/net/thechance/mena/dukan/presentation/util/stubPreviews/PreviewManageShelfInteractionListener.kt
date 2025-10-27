package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfInteractionListener

object PreviewManageShelfInteractionListener: ManageShelfInteractionListener {
    override fun onBackClicked() {}
    override fun onDeleteClicked() {}
    override fun onShelfNameChange(name: String) {}
    override fun onSaveClicked() {}
    override fun onDismissSnackBar() {}
}