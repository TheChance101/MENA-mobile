package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.domain.entity.Shelf

interface ApprovedDukanInteractionListener {
    fun onBackButtonClicked()
    fun onDismissSnackBar()
    fun onAddProductClicked()
    fun onEditShelfClicked()
    fun onAddShelfClicked()
    fun isShelfSelected(): (Shelf) -> Boolean
    fun onShelfSelected(shelf: Shelf): Boolean
    fun onShelfDeselected(shelf: Shelf): Boolean
    fun onShelfEnabled(shelf: Shelf): Boolean
}
