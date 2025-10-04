package net.thechance.mena.dukan.presentation.viewModel.createProduct

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitCancellation
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product_success
import mena.dukan_presentation.generated.resources.error_description_length
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.error_image_max_limit
import mena.dukan_presentation.generated.resources.error_image_size
import mena.dukan_presentation.generated.resources.error_price_invalid
import mena.dukan_presentation.generated.resources.error_price_not_positive
import mena.dukan_presentation.generated.resources.error_upload_failed
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.component.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.util.imageCrop.toPngByteArray
import net.thechance.mena.dukan.presentation.util.rounded
import net.thechance.mena.dukan.presentation.util.toFileName
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class CreateProductViewModel(
    private val productRepository: ProductRepository,
    private val shelfRepository: ShelfRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ProductUiState, CreateProductEffect>(
    initialState = ProductUiState(),
    defaultDispatcher = dispatcher
), CreateProductInteractionListener {

    init {
        getShelves()
    }

    private fun getShelves() {
        tryToExecute(
            block = shelfRepository::getMyDukanShelves,
            onSuccess = ::onGetShelvesSuccess,
            onError = ::onErrorSnackBar
        )
    }

    private fun onGetShelvesSuccess(shelves: List<Shelf>) {
        updateState {
            copy(shelves = shelves.map { it.toUiState() })
        }
    }

    override fun onBackButton() {
        emitEffect(effect = CreateProductEffect.NavigateBack)
    }

    override fun onProductNameChange(name: String) {
        updateState {
            copy(productName = name).updateButtonState()
        }
    }

    override fun onShelfSelect(shelfUiState: ShelfUiState) {
        updateState {
            copy(
                shelves = shelves.map { shelfItem ->
                    shelfItem.copy(isSelected = shelfItem.id == shelfUiState.id)
                },
                selectedShelf = shelfUiState,
            ).updateButtonState()
        }
    }

    override fun onPriceChange(price: String) {
        updateState {
            copy(
                price = price.filter { it.isDigit() || it == PRICE_DECIMAL_SEPARATOR },
            ).updateButtonState()
        }
    }

    override fun onDescriptionChange(description: String) {
        updateState {
            copy(description = description).updateButtonState()
        }
    }

    override fun onUploadImageClick(image: ImageFile) {
        tryToExecute(
            block = { onUploadImageBlock(image) },
            onError = ::onErrorSnackBar
        )
    }

    private suspend fun onUploadImageBlock(image: ImageFile) {
        if (isUploadImageValid(image).not())
            awaitCancellation()

        val imageSrc = image.toImageSrc()
        updateState {
            copy(
                selectedImage = imageSrc,
                showCropImage = true
            )
        }
    }

    private suspend fun isUploadImageValid(image: ImageFile): Boolean {
        val imageSizeInMegabyte = image.size().toDouble() / BYTES_PER_MEGABYTE
        val imageBitmap = image.toImageBitmap()
        val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
        val imageSrc = image.toImageSrc()

        return when {
            state.value.images.size >= IMAGE_MAX_LIMIT -> {
                updateState {
                    copy(
                        snackBarUiState = SnackBarUiState(
                            message = Res.string.error_image_max_limit,
                            snackBarType = SnackBarType.ERROR
                        ),
                        showSnackBar = true,
                    )
                }
                false
            }

            imageSizeInMegabyte > IMAGE_MAX_SIZE_IN_MB -> {
                updateState {
                    copy(
                        snackBarUiState = SnackBarUiState(
                            message = Res.string.error_image_size,
                            snackBarType = SnackBarType.ERROR
                        ),
                        showSnackBar = true,
                        showCropImage = false,
                        selectedImage = null
                    )
                }
                false
            }

            imageAspectRatio == IMAGE_ASPECT_RATIO -> {
                updateState {
                    copy(
                        images = images + ProductImageUi(
                            image = imageBitmap,
                            imageSizeInMegaByte = imageSizeInMegabyte.rounded(),
                            imageState = ProductImageState.SUCCESS,
                        )
                    ).updateButtonState()
                }
                false
            }

            imageSrc == null -> {
                updateState {
                    copy(
                        snackBarUiState = SnackBarUiState(
                            message = Res.string.error_upload_failed,
                            snackBarType = SnackBarType.ERROR
                        ),
                        showSnackBar = true,
                    )
                }
                false
            }

            else -> true
        }

    }

    fun onCroppedImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            block = { onCroppedImageBlock(imageBitmap) },
            onSuccess = ::onCroppedImageSuccess,
            onError = ::onErrorSnackBar
        )
    }

    private fun onCroppedImageBlock(imageBitmap: ImageBitmap): ProductImageUi {
        updateState {
            copy(
                selectedImage = null,
                showCropImage = false
            )
        }

        val imageByteArray = imageBitmap.toPngByteArray().size.toDouble()
        val imageSizeInMegabyte = imageByteArray / BYTES_PER_MEGABYTE

        return ProductImageUi(
            image = imageBitmap,
            imageSizeInMegaByte = imageSizeInMegabyte.rounded(),
            imageState = ProductImageState.SUCCESS,
        )
    }

    private fun onCroppedImageSuccess(productImage: ProductImageUi) {
        updateState {
            copy(images = images + productImage).updateButtonState()
        }
    }

    override fun onCropImageBackClick() {
        updateState {
            copy(
                selectedImage = null,
                showCropImage = false
            )
        }
    }

    override fun onCancelImageClick(image: ImageBitmap) {
        updateState {
            copy(
                images = images.filter { it.image != image }
            ).updateButtonState()
        }
    }

    override fun onAddProductClick() {
        tryToExecute(
            block = ::onAddProductBlock,
            onSuccess = ::onAddProductSuccess,
            onError = ::onAddProductError
        )
    }

    private suspend fun onAddProductBlock() {
        val productErrorMessage = getProductValidationError(productUiState = state.value)
        if (isProductDetailsValid(productErrorMessage).not()) awaitCancellation()

        updateState {
            copy(
                images = images.map { it.copy(imageState = ProductImageState.LOADING) },
                isAddButtonLoading = true,
                isUploadingImageEnabled = false,
                isTextFieldEnabled = false,
                isCancelImageEnabled = false
            )
        }

        val productId = productRepository.createProduct(
            params = state.value.toCreateProductParam(state.value.selectedShelf!!.id)
        )

        productRepository.uploadProductImages(
            fileName = state.value.images.map {
                state.value.productName.trim().replace(" ","_") +
                        it.image.toPngByteArray().toFileName()
            },
            fileBytes = state.value.images.map { it.image.toPngByteArray() },
            productId = productId
        )
    }

    private fun onAddProductSuccess(unit: Unit) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = Res.string.add_product_success,
                    snackBarType = SnackBarType.SUCCESS
                ),
                showSnackBar = true,
                isAddButtonLoading = false,
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
            )
        }
        emitEffect(effect = CreateProductEffect.NavigateToManagementProductMyDukan)
    }

    private fun onAddProductError(throwable: Throwable) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = Res.string.error_general,
                    snackBarType = SnackBarType.ERROR
                ),
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
                showSnackBar = true,
                isAddButtonLoading = false,
                isUploadingImageEnabled = true,
                isTextFieldEnabled = true,
                isCancelImageEnabled = true
            )
        }
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                showSnackBar = false,
                snackBarUiState = null
            )
        }
    }

    private fun onErrorSnackBar(throwable: Throwable) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = Res.string.error_general,
                    snackBarType = SnackBarType.ERROR
                ),
                showSnackBar = true,
            )
        }
    }

    private fun ProductUiState.updateButtonState(): ProductUiState {
        return copy(isAddButtonEnabled = isProductValid(this))
    }

    private fun isProductDetailsValid(productErrorMessage: StringResource?): Boolean {
        return if (productErrorMessage != null) {
            updateState {
                copy(
                    snackBarUiState = SnackBarUiState(
                        message = productErrorMessage,
                        snackBarType = SnackBarType.ERROR
                    ),
                    showSnackBar = true,
                )
            }
            false
        } else {
            true
        }
    }

    private fun isProductValid(productUiState: ProductUiState): Boolean {
        return when {
            productUiState.productName.trim().isEmpty() -> false
            productUiState.selectedShelf == null -> false
            productUiState.price.isEmpty() -> false
            productUiState.description.trim().isEmpty() -> false
            productUiState.images.map { it.image }.isEmpty() -> false
            else -> true
        }
    }

    private fun getProductValidationError(productUiState: ProductUiState): StringResource? {
        return when {
            productUiState.price.toDoubleOrNull() == null -> Res.string.error_price_invalid
            productUiState.price.toDouble() < PRICE_EXCLUSIVE_LOWER_BOUND -> Res.string.error_price_not_positive
            productUiState.description.length !in MIN_DESCRIPTION_LENGTH..MAX_DESCRIPTION_LENGTH -> Res.string.error_description_length
            else -> null
        }
    }

    companion object {
        const val IMAGE_ASPECT_RATIO = 1F
        const val IMAGE_MAX_LIMIT = 10
        const val IMAGE_MAX_SIZE_IN_MB = 5
        const val BYTES_PER_MEGABYTE = 1024 * 1024

        const val MIN_DESCRIPTION_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 3000
        const val PRICE_EXCLUSIVE_LOWER_BOUND = 0.0
        const val PRICE_DECIMAL_SEPARATOR = '.'
    }
}

