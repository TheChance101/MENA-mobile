package net.thechance.mena.dukan.presentation.viewModel.createProduct

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.dukan.presentation.util.file.ImageFile

interface CreateProductInteractionListener {
    fun onBackButton()
    fun onProductNameChange(name: String)
    fun onShelfSelect(shelfUiState: CreateProductUiState.ShelfUiState)
    fun onPriceChange(price: String)
    fun onDescriptionChange(description: String)
    fun onUploadImageClick(image: ImageFile)
    fun onCancelImageClick(image: ImageBitmap)
    fun onAddProductClick()
    fun onDismissSnackBar()
    fun onCropImageBackClick()
}
