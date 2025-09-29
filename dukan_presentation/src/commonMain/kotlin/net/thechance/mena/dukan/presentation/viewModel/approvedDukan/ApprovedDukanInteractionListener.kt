package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf

interface ApprovedDukanInteractionListener {
    fun onBackButtonClicked()
    fun onDismissSnackBar()
    fun onDismissDeleteShelfConfirmationDialog()
    fun onShowDeleteShelfConfirmationDialog()
    fun deleteShelf(shelfId: String)
    fun onAddProductClicked()
    fun onProductClick(product: Product)
    fun onEditShelfClicked()
    fun onAddShelfClicked()
    fun onShelfAddedSuccessfully()
    fun isShelfSelected(): (Shelf) -> Boolean
    fun onShelfSelected(shelf: Shelf): Boolean
    fun onShelfDeselected(shelf: Shelf): Boolean
    fun onShelfEnabled(shelf: Shelf): Boolean
}
