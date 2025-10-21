package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createProduct.ProductUiState

object PreviewCreateProductInterfaceListener: CreateProductInteractionListener {
    override fun onBackClicked() {

    }

    override fun onProductNameChange(name: String) {
    }

    override fun onShelfSelect(shelfUiState: ProductUiState.ShelfUiState) {
    }

    override fun onPriceChange(price: String) {
    }

    override fun onDescriptionChange(description: String) {
    }

    override fun onUploadImageClicked(image: ImageFile) {
    }

    override fun onCancelImageClicked(image: ImageBitmap) {
    }

    override fun onAddProductClicked() {
    }

    override fun onDismissSnackBar() {
    }

    override fun onCropImageBackClicked() {
    }
}