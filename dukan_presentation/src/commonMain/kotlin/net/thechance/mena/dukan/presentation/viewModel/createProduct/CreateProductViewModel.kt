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
import mena.dukan_presentation.generated.resources.invalid_image_format
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.price_after_discount_bigger_than_base_price
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.InvalidImageFormatException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.UploadingFailedException
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.util.filterPriceInput
import net.thechance.mena.dukan.presentation.util.imageCrop.toPngByteArray
import net.thechance.mena.dukan.presentation.util.rounded
import net.thechance.mena.dukan.presentation.util.toFileName
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class CreateProductViewModel(
    private val productRepository: ProductRepository,
    private val shelfRepository: ShelfRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<CreateProductUiState, CreateProductEffect>(
    initialState = CreateProductUiState(),
    defaultDispatcher = dispatcher
), CreateProductInteractionListener {

    init {
        getShelves()
    }

    private fun getShelves() {
        tryToExecute(
            onStart = { updateState { copy(isShelvesLoading = true) } },
            block = shelfRepository::getMyDukanShelves,
            onSuccess = ::onGetShelvesSuccess,
            onError = ::onErrorGettingShelves
        )
    }

    private fun onGetShelvesSuccess(shelves: List<Shelf>) {
        updateState {
            copy(shelves = shelves.map { it.toUiState() }, isShelvesLoading = false)
        }
    }

    override fun onBackClicked() {
        emitEffect(effect = CreateProductEffect.NavigateBack)
    }

    override fun onProductNameChange(name: String) {
        updateState {
            copy(productName = name).updateButtonState()
        }
    }

    override fun onShelfSelect(shelfUiState: CreateProductUiState.ShelfUiState) {
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
                price = filterPriceInput(price)
            ).updateButtonState()
        }
    }

    override fun onPriceAfterDiscountChange(price: String) {
        updateState {
            copy(
                priceAfterDiscount = filterPriceInput(price)
            ).updateButtonState()
        }
    }

    override fun onDescriptionChange(description: String) {
        updateState {
            copy(description = description).updateButtonState()
        }
    }

    override fun onUploadImageClicked(image: ImageFile) {
        tryToExecute(
            block = { onUploadImageBlock(image) },
            onError = ::onErrorUploadingImages
        )
    }

    private suspend fun onUploadImageBlock(image: ImageFile) {
        if (isUploadImageValidToCrop(image).not())
            awaitCancellation()

        val imageSrc = image.toImageSrc()
        updateState {
            copy(
                selectedImage = imageSrc,
                showCropImage = true
            )
        }
    }

    private suspend fun isUploadImageValidToCrop(image: ImageFile): Boolean {
        val imageSizeInMegabyte = image.size().toDouble() / BYTES_PER_MEGABYTE
        val imageBitmap = image.toImageBitmap()
        val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
        val imageSrc = image.toImageSrc()

        return when {
            state.value.images.size >= IMAGE_MAX_LIMIT -> handleUploadImageError(resErrorMessage = Res.string.error_image_max_limit)
            imageSizeInMegabyte > IMAGE_MAX_SIZE_IN_MB -> handleUploadImageError(resErrorMessage = Res.string.error_image_size)
            imageSrc == null -> handleUploadImageError(resErrorMessage = Res.string.error_upload_failed)
            imageAspectRatio == IMAGE_ASPECT_RATIO -> addImageToList(
                imageBitmap = imageBitmap,
                imageSizeInMegabyte = imageSizeInMegabyte
            )

            else -> true
        }
    }

    private fun handleUploadImageError(resErrorMessage: StringResource): Boolean {
        showSnackBar(message = resErrorMessage, type = SnackBarType.ERROR)
        updateState {
            copy(
                showCropImage = false,
                selectedImage = null
            )
        }
        return false
    }

    private fun addImageToList(imageBitmap: ImageBitmap, imageSizeInMegabyte: Double): Boolean {
        updateState {
            copy(
                images = images + CreateProductUiState.ProductImageUi(
                    image = imageBitmap,
                    imageSizeInMegaByte = imageSizeInMegabyte.rounded(),
                    imageState = ProductImageState.SUCCESS,
                )
            ).updateButtonState()
        }
        return false
    }

    fun onCroppedImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            block = { onCroppedImageBlock(imageBitmap) },
            onSuccess = ::onCroppedImageSuccess,
        )
    }

    private fun onCroppedImageBlock(imageBitmap: ImageBitmap): CreateProductUiState.ProductImageUi {
        updateState {
            copy(
                selectedImage = null,
                showCropImage = false
            )
        }
        val imageByteArray = imageBitmap.toPngByteArray().size.toDouble()
        val imageSizeInMegabyte = imageByteArray / BYTES_PER_MEGABYTE

        return CreateProductUiState.ProductImageUi(
            image = imageBitmap,
            imageSizeInMegaByte = imageSizeInMegabyte.rounded(),
            imageState = ProductImageState.SUCCESS,
        )
    }

    private fun onCroppedImageSuccess(productImage: CreateProductUiState.ProductImageUi) {
        updateState {
            copy(images = images + productImage).updateButtonState()
        }
    }

    override fun onCropImageBackClicked() {
        updateState {
            copy(
                selectedImage = null,
                showCropImage = false
            )
        }
    }

    override fun onCancelImageClicked(image: ImageBitmap) {
        updateState {
            copy(
                images = images.filter { it.image != image }
            ).updateButtonState()
        }
    }

    override fun onAddProductClicked() {
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
        uploadProductImages()
    }

    private suspend fun uploadProductImages() {
        val productId = productRepository.createProduct(
            params = state.value.toCreateProductParam(state.value.selectedShelf!!.id)
        )

        productRepository.uploadProductImages(
            fileName = state.value.images.map {
                state.value.productName.trim().replace(" ", "_") +
                        it.image.toPngByteArray().toFileName()
            },
            fileBytes = state.value.images.map { it.image.toPngByteArray() },
            productId = productId
        )
    }

    private fun onAddProductSuccess(unit: Unit) {
        showSnackBar(message = Res.string.add_product_success, type = SnackBarType.SUCCESS)
        updateState {
            copy(
                isAddButtonLoading = false,
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
            )
        }
        emitEffect(effect = CreateProductEffect.NavigateToManageDukanProducts)
    }

    private fun onAddProductError(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.error_general
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
        updateState {
            copy(
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
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

    private fun onErrorGettingShelves(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.error_general
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
        updateState { copy(isShelvesLoading = false) }
    }

    private fun onErrorUploadingImages(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            is UploadingFailedException -> Res.string.error_upload_failed
            is InvalidImageFormatException -> Res.string.invalid_image_format
            else -> Res.string.error_general
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun CreateProductUiState.updateButtonState(): CreateProductUiState {
        return copy(isAddButtonEnabled = isProductValid(this))
    }

    private fun isProductDetailsValid(productErrorMessage: StringResource?): Boolean {
        return if (productErrorMessage != null) {
            showSnackBar(message = productErrorMessage, type = SnackBarType.ERROR)
            false
        } else {
            true
        }
    }

    private fun isProductValid(productUiState: CreateProductUiState): Boolean {
        return when {
            productUiState.productName.trim().isEmpty() -> false
            productUiState.selectedShelf == null -> false
            productUiState.price.isEmpty() -> false
            productUiState.description.trim().isEmpty() -> false
            productUiState.images.map { it.image }.isEmpty() -> false
            else -> true
        }
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                ),
                showSnackBar = true
            )
        }
    }

    private fun getProductValidationError(productUiState: CreateProductUiState): StringResource? {
        return when {
            productUiState.price.toDoubleOrNull() == null -> Res.string.error_price_invalid
            productUiState.price.toDouble() <= PRICE_EXCLUSIVE_LOWER_BOUND -> Res.string.error_price_not_positive
            productUiState.description.length !in MIN_DESCRIPTION_LENGTH..MAX_DESCRIPTION_LENGTH -> Res.string.error_description_length
            productUiState.priceAfterDiscount > productUiState.price -> Res.string.price_after_discount_bigger_than_base_price
            else -> null
        }
    }

    companion object {
        const val IMAGE_ASPECT_RATIO = 1F
        const val IMAGE_MAX_LIMIT = 10
        const val IMAGE_MAX_SIZE_IN_MB = 5
        const val BYTES_PER_MEGABYTE = 1024 * 1024
        const val MIN_DESCRIPTION_LENGTH = 25
        const val MAX_DESCRIPTION_LENGTH = 3000
        const val PRICE_EXCLUSIVE_LOWER_BOUND = 0.0
    }
}

