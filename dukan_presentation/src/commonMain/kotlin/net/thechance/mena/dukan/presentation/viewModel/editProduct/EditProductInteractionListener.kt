package net.thechance.mena.dukan.presentation.viewModel.editProduct

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState

interface EditProductInteractionListener {
    fun onBackClicked()
    fun onDeleteProductClicked()
    fun onDismissDeleteDialog()
    fun onDeleteConfirmed()
    fun onProductNameChange(name: String)
    fun onShelfSelect(shelfUiState: CreateProductUiState.ShelfUiState)
    fun onPriceChange(price: String)
    fun onPriceAfterDiscountChange(price: String)
    fun onDescriptionChange(description: String)
    fun onUploadImageClicked(image: ImageFile)
    fun onCancelImageClicked(image: ImageBitmap)
    fun onCancelExistingImageUrl(url: String)
    fun onSaveProductClicked()
    fun onDismissSnackBar()
    fun onCropImageBackClicked()
    fun onOutOfStockChange(isOutOfStock: Boolean)
}



