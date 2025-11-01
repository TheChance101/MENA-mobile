package net.thechance.mena.dukan.presentation.viewModel.createProduct

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageModel
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class CreateProductUiState(
    val isAddButtonLoading: Boolean = false,
    val isAddButtonEnabled: Boolean = false,
    val shelves: List<ShelfUiState> = emptyList(),
    val isShelvesLoading: Boolean = false,
    val selectedShelf: ShelfUiState? = null,
    val productName: String = "",
    val price: String = "",
    val description: String = "",
    val images: List<ProductImageUi> = emptyList(),
    val selectedImage: ImageSrc? = null,
    val isUploadingImageEnabled: Boolean = true,
    val showCropImage: Boolean = false,
    val snackBarUiState: SnackBarUiState? = null,
    val showSnackBar: Boolean = false,
    val isTextFieldEnabled: Boolean = true,
    val isCancelImageEnabled: Boolean = true,
) {
    data class ShelfUiState(
        val id: String = "",
        val name: String = "",
        val isSelected: Boolean = false,
    )

    @OptIn(ExperimentalTime::class)
    data class ProductImageUi(
        override val id: Long = Clock.System.now().toEpochMilliseconds(),
        override val image: ImageBitmap,
        override val imageUrl: String? = null,
        override val imageSizeInMegaByte: Double,
        override val imageState: ProductImageState,
        override val errorMessage: String? = null,
    ) : ProductImageModel
}