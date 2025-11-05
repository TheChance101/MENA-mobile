package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import org.jetbrains.compose.resources.StringResource

interface ManageDukanInteractionListener {
    fun onBackClicked()
    fun onDismissSnackBar()
    fun onShelfAdded(message: StringResource, type: SnackBarType)
    fun onDismissDeleteShelfConfirmationDialog()
    fun onShowDeleteShelfDialog(shelfId: String)
    fun onDeleteConfirmed(shelfId: String)
    fun onAddProductClicked()
    fun onProductClicked(product: ManageDukanUiState.ProductUiState)
    fun onEditProductClicked(productId: String)
    fun onEditShelfClicked()
    fun onAddShelfClicked()
    fun isShelfSelected(shelf: ManageDukanUiState.ShelfUiState): Boolean
    fun onShelfSelected(shelf: ManageDukanUiState.ShelfUiState)
}