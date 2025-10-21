package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import net.thechance.mena.dukan.presentation.component.SnackBarType
import org.jetbrains.compose.resources.StringResource

interface ManageDukanInteractionListener {
    fun onBackButtonClicked()
    fun onDismissSnackBar()
    fun onShelfAdded(message: StringResource, type: SnackBarType)
    fun onDismissDeleteShelfConfirmationDialog()
    fun onShowDeleteShelfDialog(shelfId: String)
    fun onDeleteConfirmed(shelfId: String)
    fun onAddProductClicked()
    fun onProductClick(product: ManageDukanUiState.ProductUiState)
    fun onEditShelfClicked()
    fun onAddShelfClicked()
    fun isShelfSelected(shelf: ManageDukanUiState.ShelfUiState): Boolean
    fun onShelfSelected(shelf: ManageDukanUiState.ShelfUiState)
}