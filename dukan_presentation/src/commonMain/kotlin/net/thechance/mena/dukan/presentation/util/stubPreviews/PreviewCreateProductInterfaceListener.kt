package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState.ShelfUiState

object PreviewCreateProductInterfaceListener: CreateProductInteractionListener {
    override fun onBackButton() {

    }

    override fun onProductNameChange(name: String) {
    }

    override fun onShelfSelect(shelfUiState: ShelfUiState) {
    }

    override fun onPriceChange(price: String) {
    }

    override fun onDescriptionChange(description: String) {
    }

    override fun onUploadImageClick(image: ImageFile) {
    }

    override fun onCancelImageClick(image: ImageBitmap) {
    }

    override fun onAddProductClick() {
    }

    override fun onDismissSnackBar() {
    }

    override fun onCropImageBackClick() {
    }
}