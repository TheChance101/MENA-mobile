package net.thechance.mena.dukan.presentation.viewModel.editProduct

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.dukan.presentation.util.file.ImageFile

interface EditProductInteractionListener {
    fun onBackClicked()
    fun onDeleteProductClicked()
    fun onProductNameChange(name: String)
    fun onShelfSelect(shelfUiState: EditProductUiState.ShelfUiState)
    fun onPriceChange(price: String)
    fun onDescriptionChange(description: String)
    fun onUploadImageClicked(image: ImageFile)
    fun onCancelImageClicked(image: ImageBitmap)
    fun onCancelExistingImageUrl(url: String)
    fun onSaveProductClicked()
    fun onDismissSnackBar()
    fun onCropImageBackClicked()
}



