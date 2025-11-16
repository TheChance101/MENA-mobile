package net.thechance.mena.dukan.presentation.viewModel.createProduct

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.dukan.presentation.util.file.ImageFile

interface CreateProductInteractionListener {
    fun onBackClicked()
    fun onProductNameChange(name: String)
    fun onShelfSelect(shelfUiState: CreateProductUiState.ShelfUiState)
    fun onPriceChange(price: String)
    fun onPriceAfterDiscountChange(price: String)
    fun onDescriptionChange(description: String)
    fun onUploadImageClicked(image: ImageFile)
    fun onCancelImageClicked(image: ImageBitmap)
    fun onAddProductClicked()
    fun onDismissSnackBar()
    fun onCropImageBackClicked()
}
