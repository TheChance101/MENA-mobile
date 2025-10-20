package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ShelfUiState
import org.jetbrains.compose.resources.StringResource

object PreviewManageDukanInteractionListener : ManageDukanInteractionListener {
    override fun onBackButtonClicked() {}
    override fun onDismissSnackBar() {}
    override fun onShelfAdded(
        message: StringResource,
        type: SnackBarType
    ) {
    }

    override fun onDismissDeleteShelfConfirmationDialog() {}
    override fun onShowDeleteShelfDailog(shelfId: String) {}
    override fun onDeleteConfirmed(shelfId: String) {}
    override fun onAddProductClicked() {}
    override fun onEditShelfClicked() {}
    override fun onAddShelfClicked() {}
    override fun onProductClick(product: ProductUiState) {}
    override fun onShelfAddedSuccessfully() {}
    override fun isShelfSelected(shelf: ShelfUiState): Boolean = false
    override fun onShelfSelected(shelf: ShelfUiState) {}
}
